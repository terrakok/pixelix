package com.daniebeler.pfpixelix.utils

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

actual fun KmpContext.openUrl(url: String) {
    val intent = CustomTabsIntent.Builder().build()
    intent.launchUrl(this, Uri.parse(url))
}