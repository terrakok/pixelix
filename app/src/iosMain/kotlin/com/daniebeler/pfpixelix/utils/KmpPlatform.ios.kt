package com.daniebeler.pfpixelix.utils

import coil3.PlatformContext
import okio.Path

actual abstract class KmpUri {
    actual abstract override fun toString(): String
}
actual abstract class KmpContext
actual val KmpContext.coilContext: PlatformContext get() = PlatformContext.INSTANCE
actual val KmpContext.imageCacheDir: Path get() = TODO("Not yet implemented")

actual interface KmpImageBitmap