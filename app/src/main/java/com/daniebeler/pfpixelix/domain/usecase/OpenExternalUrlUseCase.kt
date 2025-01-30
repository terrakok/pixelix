package com.daniebeler.pfpixelix.domain.usecase

import android.content.Context
import com.daniebeler.pfpixelix.domain.repository.StorageRepository
import com.daniebeler.pfpixelix.utils.Navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class OpenExternalUrlUseCase(
    private val repository: StorageRepository
) {
    operator fun invoke(context: Context, url: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val useInAppBrowser = repository.getUseInAppBrowser().first()
            if (useInAppBrowser) {
                Navigate.openUrlInApp(context, url)
            } else {
                Navigate.openUrlInBrowser(context, url)
            }
        }
    }
}