package com.daniebeler.pfpixelix.utils

import androidx.compose.runtime.staticCompositionLocalOf
import coil3.PlatformContext
import okio.Path

expect abstract class KmpUri {
    abstract override fun toString(): String
}
expect abstract class KmpContext
expect val KmpContext.coilContext: PlatformContext
expect val KmpContext.imageCacheDir: Path
expect val KmpContext.dataStoreDir: Path

val LocalKmpContext = staticCompositionLocalOf<KmpContext> { error("no KmpContext") }
