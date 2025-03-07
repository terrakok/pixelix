package com.daniebeler.pfpixelix.domain.service.platform

import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.utils.KmpContext
import me.tatarka.inject.annotations.Inject
import java.awt.Desktop
import java.net.URI

@Inject
actual class Platform actual constructor(
    private val context: KmpContext,
    private val prefs: UserPreferences
) {
    actual fun openUrl(url: String) {
        Desktop.getDesktop().browse(URI(url))
    }

    actual fun shareText(text: String) {}

    actual fun getAppVersion(): String {
        return "1.0.0"
    }

    actual fun pinWidget() {}
}
