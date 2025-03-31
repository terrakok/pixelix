package com.daniebeler.pfpixelix.domain.service.platform

import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.utils.KmpContext
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.Foundation.NSURL.Companion.URLWithString
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Inject
actual class Platform actual constructor(
    private val context: KmpContext,
    private val prefs: UserPreferences
) {
    actual fun openUrl(url: String) {
        if (prefs.useInAppBrowser) {
            val safariViewController = SFSafariViewController(uRL = NSURL(string = url))
            UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
                safariViewController,
                animated = true,
                null
            )

        } else {
            UIApplication.sharedApplication.openURL(
                url = URLWithString(url)!!,
                options = emptyMap<Any?, Any>(),
                completionHandler = null
            )
        }
    }

    actual fun dismissBrowser() {
        if (prefs.useInAppBrowser) {
            UIApplication.sharedApplication.keyWindow?.rootViewController?.dismissModalViewControllerAnimated(true)
        }
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
