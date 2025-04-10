package com.daniebeler.pfpixelix.domain.service.platform

import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.utils.KmpContext
import me.tatarka.inject.annotations.Inject

@Inject
expect class Platform(
    context: KmpContext,
    prefs: UserPreferences
) {
    fun openUrl(url: String)
    fun dismissBrowser()
    fun shareText(text: String)
    fun getAppVersion(): String
    fun pinWidget()
}
