package com.daniebeler.pfpixelix.domain.usecase

import com.daniebeler.pfpixelix.domain.repository.StorageRepository
import me.tatarka.inject.annotations.Inject

@Inject
class StoreHideAltTextButtonUseCase(
    private val storageRepository: StorageRepository
) {

    suspend operator fun invoke(hideAltTextButton: Boolean) {
        return storageRepository.storeHideAltTextButton(hideAltTextButton)
    }
}