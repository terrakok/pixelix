package com.daniebeler.pfpixelix.utils

interface ContextNavigation {
    fun updateAuthToV2(baseUrl: String, accessToken: String)
    fun gotoLoginActivity(isAbleToGotBack: Boolean)
    fun redirect()
    fun openUrlInApp(url: String)
}
