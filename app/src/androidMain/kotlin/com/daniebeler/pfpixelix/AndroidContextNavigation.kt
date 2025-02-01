package com.daniebeler.pfpixelix;

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.daniebeler.pfpixelix.utils.ContextNavigation

class AndroidContextNavigation(
    var context: Context
) : ContextNavigation {
    override fun updateAuthToV2(baseUrl: String, accessToken: String) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra("base_url", baseUrl)
        intent.putExtra("access_token", accessToken)
        context.startActivity(intent)
    }

    override fun gotoLoginActivity(isAbleToGotBack: Boolean) {
        val intent = if (isAbleToGotBack) {
            Intent(context, LoginActivity::class.java)
        } else {
            Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
        context.startActivity(intent)
    }

    override fun redirect() {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    override fun openUrlInApp(url: String) {
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(context, Uri.parse(url))
    }
}
