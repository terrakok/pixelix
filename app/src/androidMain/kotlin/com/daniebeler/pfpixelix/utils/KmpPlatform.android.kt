package com.daniebeler.pfpixelix.utils

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import coil3.PlatformContext
import okio.Path
import okio.Path.Companion.toPath

actual typealias KmpUri = Uri

actual typealias KmpContext = Context
actual val KmpContext.coilContext: PlatformContext get() = this
actual val KmpContext.imageCacheDir: Path get() = cacheDir.path.toPath()

actual typealias KmpImageBitmap = ImageBitmap