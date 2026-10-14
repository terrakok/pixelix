package com.daniebeler.pfpixelix.utils

import androidx.compose.ui.graphics.ImageBitmap

internal actual suspend fun decodeBlurHash(
    blurHash: String?,
    width: Int?,
    height: Int?,
    punch: Float
): ImageBitmap? = BlurHashDecoder.decode(blurHash, width, height, punch)
