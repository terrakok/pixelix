package com.daniebeler.pfpixelix.domain.service.file

import com.daniebeler.pfpixelix.utils.KmpUri
import okio.Path

interface FileService {
    val dataStoreDir: Path
    val imageCacheDir: Path

    fun getFile(uri: KmpUri): PlatformFile?
    fun downloadFile(name: String?, url: String)
    fun getCacheSizeInBytes(): Long
    fun cleanCache()
}

interface PlatformFile {
    fun isExist(): Boolean
    fun getName(): String
    fun getSize(): Long
    fun getMimeType(): String
    suspend fun readBytes(): ByteArray
    suspend fun getThumbnail(): ByteArray?
}