package com.daniebeler.pfpixelix.domain.usecase

import com.daniebeler.pfpixelix.utils.KmpContext
import me.tatarka.inject.annotations.Inject

@Inject
actual class EnableCustomAppIconUseCase {
    actual operator fun invoke(
        context: KmpContext,
        name: String
    ) {
    }
}