package com.daniebeler.pfpixelix.domain.usecase

import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.KmpImageBitmap
import me.tatarka.inject.annotations.Inject

@Inject
expect class GetActiveAppIconUseCase {
    operator fun invoke(context: KmpContext): KmpImageBitmap?
}