package com.daniebeler.pfpixelix.domain.usecase

import com.daniebeler.pfpixelix.domain.repository.StorageRepository
import com.daniebeler.pfpixelix.utils.KmpContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
actual class OpenExternalUrlUseCase actual constructor(
    private val repository: StorageRepository
) {
    actual operator fun invoke(context: KmpContext, url: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val useInAppBrowser = repository.getUseInAppBrowser().first()
            if (useInAppBrowser) {
//                Navigate.openUrlInApp(context, url) todo
            } else {
//                Navigate.openUrlInBrowser(context, url)
            }
        }
    }
}