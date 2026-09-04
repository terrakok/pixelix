package com.daniebeler.pfpixelix

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.useSnapshotCache
import androidx.compose.ui.window.ComposeViewport
import coil3.SingletonImageLoader
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.create
import com.daniebeler.pfpixelix.domain.service.icon.WebAppIconManager
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.configureLogger

@OptIn(ExperimentalComposeUiApi::class)
fun webApp() {
    // Avoid the experimental RenderNode snapshot path involved in the current Skiko WASM crash.
    // The failure surfaces while replaying a cached layer in SkCanvas::saveLayer/malloc. This flag
    // must be set before ComposeViewport creates the root owner and its SkiaGraphicsContext.
    ComposeUiFlags.useSnapshotCache = false

    val appComponent = AppComponent.create(
        object : KmpContext() {},
        WebAppIconManager()
    )

    configureLogger(false)

    SingletonImageLoader.setSafe {
        appComponent.provideImageLoader()
    }

    setOAuthRedirectCallback {
        appComponent.systemUrlHandler.onRedirect(it)
    }
    setOAuthRedirectMessageListener {
        appComponent.systemUrlHandler.onRedirect(it)
    }
    setOAuthRedirectBroadcastListener {
        appComponent.systemUrlHandler.onRedirect(it)
    }

    ComposeViewport(
        configure = {
            enableBrowserWindowInsets = true
        }
    ) {
        App(
            appComponent = appComponent,
            exitApp = { /* browser Back leaves the app from its root */ },
        )
    }
}


private fun setOAuthRedirectCallback(cb: (String) -> Unit): Unit =
    js("{ window.pixelixOAuthCallback = cb; }")

private fun setOAuthRedirectMessageListener(cb: (String) -> Unit): Unit = js(
    """{
        window.addEventListener('message', function(event) {
            if (event.origin !== window.location.origin) return;
            if (!event.data || event.data.type !== 'pixelix-oauth-redirect') return;
            cb(event.data.url);
        });
    }"""
)

private fun setOAuthRedirectBroadcastListener(cb: (String) -> Unit): Unit = js(
    """{
        if (typeof BroadcastChannel === 'undefined') return;
        if (window.pixelixOAuthChannel) window.pixelixOAuthChannel.close();
        var channel = new BroadcastChannel('pixelix-oauth');
        channel.onmessage = function(event) {
            if (!event.data || event.data.type !== 'pixelix-oauth-redirect') return;
            cb(event.data.url);
        };
        // Keep the channel alive for the lifetime of the PWA page.
        window.pixelixOAuthChannel = channel;
    }"""
)
