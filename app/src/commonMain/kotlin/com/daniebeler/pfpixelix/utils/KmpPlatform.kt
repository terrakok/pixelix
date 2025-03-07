package com.daniebeler.pfpixelix.utils

import coil3.PlatformContext
import io.github.vinceglb.filekit.core.PlatformFile

expect abstract class KmpUri {
    abstract override fun toString(): String
}
expect val EmptyKmpUri: KmpUri
expect fun KmpUri.getPlatformUriObject(): Any
expect fun String.toKmpUri(): KmpUri
expect fun PlatformFile.toKmpUri(): KmpUri

expect abstract class KmpContext

expect val KmpContext.coilContext: PlatformContext
