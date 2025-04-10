package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.lifecycle.ViewModel
import com.daniebeler.pfpixelix.domain.service.file.FileService
import me.tatarka.inject.annotations.Inject

@Inject
class ClearCacheViewModel(
    private val fileService: FileService
): ViewModel() {
    fun getCacheSizeInBytes() = fileService.getCacheSizeInBytes()
    fun cleanCache() {
        fileService.cleanCache()
    }
}