package com.daniebeler.pfpixelix.domain.usecase

import com.daniebeler.pfpixelix.domain.repository.StorageRepository
import me.tatarka.inject.annotations.Inject

@Inject
class StoreUseInAppBrowserUseCase(
    private val storageRepository: StorageRepository
) {

    suspend operator fun invoke(useInAppBrowser: Boolean) {
        return storageRepository.storeUseInAppBrowser(useInAppBrowser)
    }
}