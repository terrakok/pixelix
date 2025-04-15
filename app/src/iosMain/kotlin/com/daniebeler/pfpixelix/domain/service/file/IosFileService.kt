package com.daniebeler.pfpixelix.domain.service.file

import com.daniebeler.pfpixelix.utils.KmpUri
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.path
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import okio.Path
import okio.Path.Companion.toPath
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFURLRef
import platform.CoreGraphics.CGDataProviderCopyData
import platform.CoreGraphics.CGImageGetDataProvider
import platform.CoreServices.UTTypeCopyPreferredTagWithClass
import platform.CoreServices.UTTypeCreatePreferredIdentifierForTag
import platform.CoreServices.kUTTagClassFilenameExtension
import platform.CoreServices.kUTTagClassMIMEType
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSDictionary
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSize
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.fileSize
import platform.ImageIO.CGImageSourceCreateThumbnailAtIndex
import platform.ImageIO.CGImageSourceCreateWithURL
import platform.ImageIO.kCGImageSourceCreateThumbnailFromImageAlways
import platform.ImageIO.kCGImageSourceCreateThumbnailWithTransform
import platform.ImageIO.kCGImageSourceThumbnailMaxPixelSize
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
class IosFileService : FileService() {
    override fun getFile(uri: KmpUri): PlatformFile? {
        return IosFile(uri).takeIf { it.isExist() }
    }

    override fun getCacheSizeInBytes(): Long {
        val fm = NSFileManager.defaultManager()
        val files = fm.subpathsOfDirectoryAtPath(imageCacheDir.toString(), null).orEmpty()
        var result = 0uL
        files.map { file ->
            val dict = fm.fileAttributesAtPath(
                imageCacheDir.resolve(file.toString()).toString(),
                true
            ) as NSDictionary
            result += dict.fileSize()
        }
        return result.toLong()
    }

    override fun cleanCache() {
        val fm = NSFileManager.defaultManager()
        fm.removeItemAtPath(imageCacheDir.toString(), null)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class IosFile(
    private val uri: KmpUri
) : PlatformFile {
    override fun isExist(): Boolean =
        getName() != "IosFile:unknown"

    override fun getName(): String {
        return uri.url.lastPathComponent() ?: "IosFile:unknown"
    }

    override fun getSize(): Long {
        val path = uri.url.path ?: return 0L
        val fm = NSFileManager.defaultManager
        val attr = fm.attributesOfItemAtPath(path, null) ?: return 0L
        return attr.getValue(NSFileSize) as Long
    }

    override fun getMimeType(): String {
        val fileExtension = uri.url.pathExtension()
        @Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
        val fileExtensionRef = CFBridgingRetain(fileExtension as NSString) as CFStringRef
        val uti = UTTypeCreatePreferredIdentifierForTag(
            kUTTagClassFilenameExtension,
            fileExtensionRef,
            null
        )
        CFRelease(fileExtensionRef)
        val mimeType = UTTypeCopyPreferredTagWithClass(uti, kUTTagClassMIMEType)
        CFRelease(uti)
        return CFBridgingRelease(mimeType) as String
    }

    override suspend fun readBytes(): ByteArray = withContext(Dispatchers.IO) {
        val data = NSData.dataWithContentsOfURL(uri.url)!!
        ByteArray(data.length.toInt()).apply {
            data.usePinned {
                memcpy(refTo(0), data.bytes, data.length)
            }
        }
    }

    override suspend fun getThumbnail(): ByteArray? = withContext(Dispatchers.IO) {
        @Suppress("UNCHECKED_CAST")
        val urlRef = CFBridgingRetain(uri.url) as CFURLRef
        val imageSource = CGImageSourceCreateWithURL(urlRef, null)!!
        val thumbnailOptions = CFDictionaryCreateMutable(
            null,
            3,
            null,
            null
        ).apply {
            CFDictionaryAddValue(
                this,
                kCGImageSourceCreateThumbnailWithTransform,
                CFBridgingRetain(NSNumber(bool = true))
            )
            CFDictionaryAddValue(
                this,
                kCGImageSourceCreateThumbnailFromImageAlways,
                CFBridgingRetain(NSNumber(bool = true))
            )
            CFDictionaryAddValue(
                this,
                kCGImageSourceThumbnailMaxPixelSize,
                CFBridgingRetain(NSNumber(512))
            )
        }

        val thumbnailSource = CGImageSourceCreateThumbnailAtIndex(
            imageSource,
            0u,
            thumbnailOptions
        )

        val data = CGDataProviderCopyData(CGImageGetDataProvider(thumbnailSource))
        val bytePointer = CFDataGetBytePtr(data)!!
        val length = CFDataGetLength(data)

        val byteArray = ByteArray(length.toInt()) { index ->
            bytePointer[index].toByte()
        }

        CFRelease(urlRef)
        CFRelease(data)
        CFRelease(thumbnailSource)

        byteArray
    }
}