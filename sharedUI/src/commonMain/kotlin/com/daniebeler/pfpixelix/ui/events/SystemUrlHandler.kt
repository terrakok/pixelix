package com.daniebeler.pfpixelix.ui.events

import androidx.compose.ui.platform.UriHandler
import com.daniebeler.pfpixelix.di.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@Inject
@SingleIn(AppScope::class)
class SystemUrlHandler {
    // Keep a callback that arrives before the auth coroutine starts waiting.
    private val redirectsChannel = Channel<String>(Channel.CONFLATED)
    val redirects = redirectsChannel.receiveAsFlow()

    var uriHandler: UriHandler? = null

    var isAuthInProgress: Boolean = false
        private set

    fun beginAuth() {
        finishAuth()
        isAuthInProgress = true
    }

    fun finishAuth() {
        isAuthInProgress = false
        while (redirectsChannel.tryReceive().isSuccess) { /* discard previous attempt */ }
    }

    fun openBrowser(url: String) {
        uriHandler?.openUri(url)
    }

    fun onRedirect(url: String) {
        if (!isAuthInProgress) return
        isAuthInProgress = false
        redirectsChannel.trySend(url)
    }

}