package com.daniebeler.pfpixelix.utils

import coil3.PlatformContext
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual abstract class KmpUri {
    actual abstract override fun toString(): String
}

actual abstract class KmpContext

actual val KmpContext.coilContext: PlatformContext get() = PlatformContext.INSTANCE
actual val KmpContext.imageCacheDir: Path get() = appDocDir().resolve("imageCache")
actual val KmpContext.dataStoreDir: Path get() = appDocDir().resolve("dataStore")

object IosContext : KmpContext()

@OptIn(ExperimentalForeignApi::class)
private fun appDocDir() = NSFileManager.defaultManager.URLForDirectory(
    directory = NSDocumentDirectory,
    inDomain = NSUserDomainMask,
    appropriateForURL = null,
    create = false,
    error = null,
)!!.path!!.toPath()