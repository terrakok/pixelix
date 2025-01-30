package com.daniebeler.pfpixelix.domain.usecase

import com.daniebeler.pfpixelix.domain.repository.StorageRepository
import me.tatarka.inject.annotations.Inject

@Inject
class StoreHideSensitiveContentUseCase(
    private val storageRepository: StorageRepository
) {

    suspend operator fun invoke(hideSensitiveContent: Boolean) {
        return storageRepository.storeHideSensitiveContent(hideSensitiveContent)
    }
}