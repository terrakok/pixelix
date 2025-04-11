package com.daniebeler.pfpixelix.domain.service.file

import com.daniebeler.pfpixelix.utils.KmpUri
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.path
import okio.Path
import okio.Path.Companion.toPath

abstract class FileService {
    val dataStoreDir: Path = FileKit.filesDir.path.toPath().resolve("datastore")
    val imageCacheDir: Path = FileKit.cacheDir.path.toPath().resolve("image_cache")

    abstract fun getFile(uri: KmpUri): PlatformFile?
    abstract fun getCacheSizeInBytes(): Long
    abstract fun cleanCache()
}

interface PlatformFile {
    fun isExist(): Boolean
    fun getName(): String
    fun getSize(): Long
    fun getMimeType(): String
    suspend fun readBytes(): ByteArray
    suspend fun getThumbnail(): ByteArray?
}