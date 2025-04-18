package com.daniebeler.pfpixelix.domain.service.file

import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.getMimeType
import com.daniebeler.pfpixelix.utils.toKmpUri
import com.daniebeler.pfpixelix.utils.toPlatformFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.isRegularFile
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.resolve
import io.github.vinceglb.filekit.size
import me.tatarka.inject.annotations.Inject
import okio.Path
import okio.Path.Companion.toPath

@Inject
class FileService(
    private val context: KmpContext
) {
    companion object {
        val dataStoreDir = FileKit.filesDir.resolve("datastore")
        val imageCacheDir = FileKit.cacheDir.resolve("image_cache")
    }

    suspend fun getCacheSizeInBytes(): Long = imageCacheDir.sizeRecursively()
    suspend fun cleanCache() {
        imageCacheDir.deleteRecursively()
    }

    fun getMimeType(file: PlatformFile): String = context.getMimeType(file.toKmpUri())

    private suspend fun PlatformFile.sizeRecursively(): Long {
        return when {
            !exists() -> 0L
            isRegularFile() -> size()
            else -> list().sumOf { it.sizeRecursively() }
        }
    }

    private suspend fun PlatformFile.deleteRecursively() {
        when {
            !exists() -> {
                return
            }
            isRegularFile() -> {
                delete(false)
            }
            else -> {
                list().forEach { it.deleteRecursively() }
                delete(false)
            }
        }
    }
}

internal fun PlatformFile(kmpUri: KmpUri): PlatformFile = kmpUri.toPlatformFile()
internal fun PlatformFile.toOkIoPath(): Path = path.toPath()
