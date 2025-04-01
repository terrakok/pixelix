package com.daniebeler.pfpixelix.domain.service.platform

import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.utils.KmpContext
import kotlinx.cinterop.ExperimentalForeignApi
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.Foundation.NSURL.Companion.URLWithString
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.*
import platform.UIKit.UIUserInterfaceIdiom
import platform.UIKit.UIUserInterfaceIdiomPad

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

    @OptIn(ExperimentalForeignApi::class)
    actual fun shareText(text: String) {
        val self = context.viewController
        val vc = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null
        )
        if (isIpad()) {
            Logger.d("share on iPad")
            vc.popoverPresentationController?.sourceView = self.view
        }
        self.presentViewController(vc, true, null)
    }

    private fun isIpad(): Boolean {
        val device = UIDevice.currentDevice
        return device.userInterfaceIdiom == UIUserInterfaceIdiomPad
    }

    actual fun getAppVersion(): String {
        return NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString").toString()
    }

    actual fun pinWidget() {}
}
