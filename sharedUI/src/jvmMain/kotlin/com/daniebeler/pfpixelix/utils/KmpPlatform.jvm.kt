package com.daniebeler.pfpixelix.utils

import coil3.PlatformContext
import com.daniebeler.pfpixelix.domain.model.request.FieldState
import com.daniebeler.pfpixelix.domain.model.request.GPSData
import com.daniebeler.pfpixelix.domain.model.request.MediaAttachmentMetadataRequest
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.exif.ExifSubIFDDescriptor
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import io.github.vinceglb.filekit.PlatformFile
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URI
import java.util.Date
import kotlin.jvm.java


private data class DesktopUri(override val uri: URI) : KmpUri() {
    override fun toString(): String = uri.toString()
}

actual abstract class KmpUri {
    abstract val uri: URI
    actual abstract override fun toString(): String
}

actual val EmptyKmpUri: KmpUri = DesktopUri(URI(""))
actual fun KmpUri.getPlatformUriObject(): Any = uri.toString()
actual fun String.toKmpUri(): KmpUri = DesktopUri(URI(this))
actual fun PlatformFile.toKmpUri(): KmpUri = DesktopUri(file.toURI())
actual fun KmpUri.toPlatformFile(): PlatformFile = PlatformFile(File(uri))

actual abstract class KmpContext

actual val KmpContext.coilContext get() = PlatformContext.INSTANCE
actual fun parseExifMetadata(bytes: ByteArray): MediaAttachmentMetadataRequest {
    try {
        val inputStream = ByteArrayInputStream(bytes)
        val metadata = ImageMetadataReader.readMetadata(inputStream)
        val ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
        val sub = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)

        val date = ifd0.getString(ExifIFD0Directory.TAG_DATETIME)
        val make = ifd0.getString(ExifIFD0Directory.TAG_MAKE)
        val model = ifd0.getString(ExifIFD0Directory.TAG_MODEL)
        val focalLength: String? = sub.getString(ExifSubIFDDirectory.TAG_FOCAL_LENGTH)
        val fNumber = sub.getString(ExifSubIFDDirectory.TAG_FNUMBER)
        val exposureTime = sub.getDoubleObject(ExifSubIFDDirectory.TAG_EXPOSURE_TIME).toString()
        val photographicSensitivity = sub.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT)
        val software = ifd0.getString(ExifIFD0Directory.TAG_SOFTWARE)
        val flash = sub.getString(ExifSubIFDDirectory.TAG_FLASH)
        val lensModel = sub.getString(ExifSubIFDDirectory.TAG_LENS_MODEL)
        val lensMake = sub.getString(ExifSubIFDDirectory.TAG_LENS_MAKE)
        val focalLenIn35mmFilm = sub.getString(ExifSubIFDDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH)

        val gpsDir = metadata.getFirstDirectoryOfType(GpsDirectory::class.java)

        val geo = gpsDir?.geoLocation
        val gpsData = if (geo != null && !geo.isZero) {
            GPSData(lat = geo.latitude.toString(), long = geo.longitude.toString())
        } else {
            GPSData(lat = "", long = "")
        }
        return MediaAttachmentMetadataRequest(
            make = FieldState(make),
            model = FieldState(model),
            createDate = FieldState(parseExifDateTime(date)),
            focalLength = FieldState(focalLength),
            fNumber = FieldState(convertApexToFNumber(
                fNumber
            )),
            exposureTime = FieldState(formatDecimalToExposureFraction(exposureTime)),
            photographicSensitivity = FieldState(photographicSensitivity),
            software = FieldState(software),
            flash = FieldState(getFlashReadableString(flash)),
            lens = FieldState(if (!lensMake.isNullOrBlank() && !lensModel.isNullOrBlank()) {
                "$lensMake $lensModel"
            } else {
                null
            }),
            focalLenIn35mmFilm = FieldState(focalLenIn35mmFilm),
            gpsData = FieldState(gpsData, isIncluded = false)
        )
    } catch (e: Throwable) {
        e.printStackTrace()
        MediaAttachmentMetadataRequest()
    }
    return MediaAttachmentMetadataRequest()
}