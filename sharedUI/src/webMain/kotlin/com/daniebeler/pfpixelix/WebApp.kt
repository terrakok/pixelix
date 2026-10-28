package com.daniebeler.pfpixelix

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import coil3.SingletonImageLoader
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.domain.service.icon.WebAppIconManager
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.configureLogger
import dev.zacsweers.metro.createGraphFactory

@OptIn(ExperimentalComposeUiApi::class)
fun webApp() {
    val appComponent = createGraphFactory<AppComponent.Factory>().create(
        object : KmpContext() {},
        WebAppIconManager()
    )

    configureLogger(false)

    SingletonImageLoader.setSafe {
        appComponent.provideImageLoader()
    }

    setOAuthRedirectListener {
        appComponent.systemUrlHandler.onRedirect(it)
    }

    ComposeViewport(
        configure = {
            isA11YEnabled = false
        }
    ) {
        App(
            appComponent = appComponent,
            exitApp = { /* browser Back leaves the app from its root */ },
        )
    }
}


private fun setOAuthRedirectListener(cb: (String) -> Unit): Unit = js(
    """{
        var key = 'pixelix-oauth-redirect';
        function consumeRedirect() {
            var redirect = window.localStorage.getItem(key);
            if (!redirect) return;
            window.localStorage.removeItem(key);
            var url;
            try { url = new URL(redirect); } catch (e) { return; }
            if (url.origin !== window.location.origin) return;
            // Cloudflare Pages redirects .html assets to extensionless URLs.
            if (url.pathname !== '/oauth-callback.html' && url.pathname !== '/oauth-callback') return;
            cb(redirect);
        }
        // Storage is durable even when Android suspends the app tab during sign in.
        window.addEventListener('storage', function(event) {
            if (event.key === key) consumeRedirect();
        });
        window.addEventListener('focus', consumeRedirect);
        window.addEventListener('pageshow', consumeRedirect);
        document.addEventListener('visibilitychange', function() {
            if (document.visibilityState === 'visible') consumeRedirect();
        });
    }"""
)
