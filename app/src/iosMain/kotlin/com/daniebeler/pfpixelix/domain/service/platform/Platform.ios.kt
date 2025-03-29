package com.daniebeler.pfpixelix.domain.service.platform

import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.utils.KmpContext
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Inject
actual class Platform actual constructor(
    private val context: KmpContext,
    private val prefs: UserPreferences
) {
    actual fun openUrl(url: String) {
        //UIApplication.sharedApplication.openURL(NSURL(string = url))
        
    }

    actual fun shareText(text: String) {
        val vc = UIActivityViewController(listOf(text), null)
        context.viewController.presentViewController(vc, true, null)
    }

    actual fun getAppVersion(): String {
        return NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString").toString()
    }

    actual fun pinWidget() {}
}
