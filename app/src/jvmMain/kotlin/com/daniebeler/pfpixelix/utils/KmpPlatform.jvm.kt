package com.daniebeler.pfpixelix.utils

import coil3.PlatformContext
import io.github.vinceglb.filekit.core.PlatformFile
import java.net.URI

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

actual abstract class KmpContext
actual val KmpContext.coilContext get() = PlatformContext.INSTANCE
