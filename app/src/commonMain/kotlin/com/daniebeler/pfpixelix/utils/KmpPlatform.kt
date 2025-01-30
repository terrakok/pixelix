package com.daniebeler.pfpixelix.utils

import coil3.PlatformContext
import okio.Path

expect abstract class KmpUri {
    abstract override fun toString(): String
}
expect abstract class KmpContext
expect val KmpContext.coilContext: PlatformContext
expect val KmpContext.imageCacheDir: Path

expect interface KmpImageBitmap