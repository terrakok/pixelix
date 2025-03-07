package com.daniebeler.pfpixelix.domain.service.file

import ca.gosyer.appdirs.AppDirs
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.utils.KmpUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Path
import okio.Path.Companion.toPath
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO

class DesktopFileService : FileService {
    private val appDirs = AppDirs("com.daniebeler.pfpixelix", null)
    private fun appDocDir() = appDirs.getUserDataDir().toPath()

    override val dataStoreDir: Path = appDocDir().resolve("dataStore")
    override val imageCacheDir: Path = appDocDir().resolve("imageCache")

    override fun getFile(uri: KmpUri): PlatformFile? {
        return DesktopFile(uri).takeIf { it.isExist() }
    }

    override fun downloadFile(name: String?, url: String) {
    }

    override fun getCacheSizeInBytes(): Long {
        return imageCacheDir.toFile().walkBottomUp().fold(0L) { acc, file -> acc + file.length() }
    }

    override fun cleanCache() {
        imageCacheDir.toFile().deleteRecursively()
    }
}

private class DesktopFile(
    uri: KmpUri
) : PlatformFile {
    private val file = File(uri.uri)

    override fun isExist(): Boolean = file.exists()
    override fun getName(): String = file.name
    override fun getSize(): Long = file.length()
    override fun getMimeType(): String = Files.probeContentType(file.toPath())

    override suspend fun readBytes(): ByteArray = withContext(Dispatchers.IO) {
        file.readBytes()
    }

    override suspend fun getThumbnail(): ByteArray? = withContext(Dispatchers.IO) {
        val thumbnail = try {
            val size = 512
            val originalImage = ImageIO.read(file)
            val aspectRatio = originalImage.width.toDouble() / originalImage.height
            val (width, height) = if (aspectRatio > 1) {
                size to (size / aspectRatio).toInt()
            } else {
                (size * aspectRatio).toInt() to size
            }
            val image = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH)
            val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            val graphics = bufferedImage.createGraphics()
            graphics.drawImage(image, 0, 0, null)
            graphics.dispose()
            bufferedImage
        } catch (e: Exception) {
            Logger.e("Failed to create thumbnail for file: ${file.name}", e)
            null
        } ?: return@withContext null

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(thumbnail, "png", outputStream)
        outputStream.toByteArray()
    }
}