package com.daniebeler.pfpixelix.utils

import platform.Foundation.NSURL.Companion.URLWithString
import platform.UIKit.UIApplication

actual fun KmpContext.openUrl(url: String) {
    try {
        UIApplication.sharedApplication.openURL(URLWithString(url)!!)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}