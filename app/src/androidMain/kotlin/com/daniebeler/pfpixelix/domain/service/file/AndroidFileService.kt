package com.daniebeler.pfpixelix.domain.service.file

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import coil3.video.videoFrameMillis
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.KmpUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import okio.Path
import okio.Path.Companion.toPath
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

class AndroidFileService(
    private val context: KmpContext
) : FileService {
    override val dataStoreDir: Path = context.filesDir.path.toPath().resolve("datastore")
    override val imageCacheDir: Path = context.cacheDir.path.toPath().resolve("image_cache")

    override fun getFile(uri: KmpUri): PlatformFile? {
        return AndroidFile(uri, context).takeIf { it.isExist() }
    }

    override fun downloadFile(name: String?, url: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var uri: Uri? = null
            val saveImageRoutine = CoroutineScope(Dispatchers.Default).launch {

                val bitmap: Bitmap? = urlToBitmap(url, context)
                if (bitmap == null) {
                    cancel("an error occured when downloading the image")
                    return@launch
                }

                println(bitmap.toString())

                uri = saveImageToMediaStore(
                    context,
                    generateUniqueName(name, false, context),
                    bitmap!!
                )
                if (uri == null) {
                    cancel("an error occured when saving the image")
                    return@launch
                }
            }

            saveImageRoutine.invokeOnCompletion { throwable ->
                CoroutineScope(Dispatchers.Main).launch {
                    uri?.let {
                        Toast.makeText(context, "Stored at: " + uri.toString(), Toast.LENGTH_LONG)
                            .show()
                    } ?: throwable?.let {
                        Toast.makeText(
                            context, "an error occurred downloading the image", Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun getCacheSizeInBytes(): Long {
        return imageCacheDir.toFile().walkBottomUp().fold(0L) { acc, file -> acc + file.length() }
    }

    override fun cleanCache() {
        imageCacheDir.toFile().deleteRecursively()
    }

    private fun generateUniqueName(
        imageName: String?, returnFullPath: Boolean, context: KmpContext
    ): String {

        val filename = "${imageName}_${Clock.System.now().epochSeconds}"

        if (returnFullPath) {
            val directory: File = context.getDir("zest", Context.MODE_PRIVATE)
            return "$directory/$filename"
        } else {
            return filename
        }
    }

    private suspend fun urlToBitmap(
        imageURL: String,
        context: KmpContext,
    ): Bitmap? {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context).data(imageURL).allowHardware(false).build()
        val result = loader.execute(request)
        if (result is SuccessResult) {
            return result.image.toBitmap()
        }
        return null
    }

    private fun saveImageToMediaStore(
        context: KmpContext,
        displayName: String,
        bitmap: Bitmap
    ): Uri? {
        val imageCollections = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.applicationContext.contentResolver
        val imageContentUri = resolver.insert(imageCollections, imageDetails) ?: return null

        return try {
            resolver.openOutputStream(imageContentUri, "w").use { os ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os!!)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageDetails.clear()
                imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(imageContentUri, imageDetails, null, null)
            }

            imageContentUri
        } catch (e: FileNotFoundException) {
            // Some legacy devices won't create directory for the Uri if dir not exist, resulting in
            // a FileNotFoundException. To resolve this issue, we should use the File API to save the
            // image, which allows us to create the directory ourselves.
            null
        }
    }
}

private class AndroidFile(
    private val uri: Uri,
    private val context: Context
) : PlatformFile {
    override fun isExist(): Boolean =
        getName() != "AndroidFile:unknown"

    override fun getName(): String = when (uri.scheme) {
        ContentResolver.SCHEME_FILE -> uri.pathSegments.last().substringBeforeLast('.')
        ContentResolver.SCHEME_CONTENT -> context.contentResolver.query(
            uri, null, null, null, null
        )?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex)
        }

        else -> null
    } ?: "AndroidFile:unknown"

    override fun getSize(): Long = when (uri.scheme) {
        ContentResolver.SCHEME_FILE -> context.contentResolver.openFileDescriptor(uri, "r")
            ?.use { it.statSize }

        ContentResolver.SCHEME_CONTENT -> context.contentResolver.query(
            uri, null, null, null, null
        )?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.SIZE)
            it.moveToFirst()
            it.getLong(nameIndex)
        }

        else -> null
    } ?: 0L

    override fun getMimeType(): String = when (uri.scheme) {
        ContentResolver.SCHEME_FILE -> {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
        }

        ContentResolver.SCHEME_CONTENT -> {
            context.contentResolver.getType(uri)
        }

        else -> null
    } ?: "image/*"

    override suspend fun readBytes(): ByteArray = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)!!.readBytes()
    }

    override suspend fun getThumbnail(): ByteArray? = withContext(Dispatchers.IO) {
        val bm = try {
            val req = ImageRequest.Builder(context).data(uri).videoFrameMillis(0).build()
            val img = SingletonImageLoader.get(context).execute(req)
            img.image?.toBitmap()
        } catch (e: Exception) {
            Logger.e("AndroidFile.getThumbnail error", e)
            null
        } ?: return@withContext null

        val stream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.toByteArray()
    }
}