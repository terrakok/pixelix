package com.daniebeler.pfpixelix.domain.usecase

import androidx.compose.ui.graphics.ImageBitmap
import com.daniebeler.pfpixelix.utils.KmpContext
import me.tatarka.inject.annotations.Inject

@Inject
actual class GetActiveAppIconUseCase {
    actual operator fun invoke(context: KmpContext): ImageBitmap? {
        //todo
        return null
    }
}