package com.daniebeler.pfpixelix.domain.service.general

import io.ktor.client.plugins.api.SendingRequest
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.Url
import io.ktor.http.encodeURLParameter
import io.ktor.http.takeFrom

internal expect val PROXY_URL: String

object NetworkProxy {
    fun url(originalUrl: String): String {
        val base = PROXY_URL.trimEnd('/')
        if (base.isEmpty() || !originalUrl.startsWith("http://", ignoreCase = true) &&
            !originalUrl.startsWith("https://", ignoreCase = true)
        ) return originalUrl
        if (Url(originalUrl).host == Url(base).host) return originalUrl
        // Encode the whole URL so ?, #, % and duplicate query parameters survive routing.
        return "$base/${originalUrl.encodeURLParameter()}"
    }
}

// Runs immediately before the engine: session host selection and auth/refresh logic
// still see the original URL. Includes redirects and retries without double wrapping.
val NetworkProxyPlugin = createClientPlugin("NetworkProxy") {
    on(SendingRequest) { request, _ ->
        request.url.takeFrom(NetworkProxy.url(request.url.buildString()))
    }
}
