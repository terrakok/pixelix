package com.daniebeler.pfpixelix

import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.create
import com.daniebeler.pfpixelix.utils.IosContext
import io.ktor.http.Url
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import platform.UIKit.UIViewController


private val mainComponent by lazy { AppComponent.create(IosContext) }

class ExternalUrlHandler {
    private val urlFlow = MutableSharedFlow<String>()
    val urls = urlFlow.asSharedFlow()

    fun onExternalUrl(url: String) {
        urlFlow.tryEmit(url)
    }
}

fun LoginViewController(externalUrlHandler: ExternalUrlHandler): UIViewController {
    val loginScreen = mainComponent.loginScreen

    return ComposeUIViewController {
        var isLoginOngoing by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            externalUrlHandler.urls.collect { url ->
                if (isLoginOngoing) {
                    loginScreen.onStart(
                        baseUrl = null,
                        accessToken = null,
                        url = Url(url),
                        redirect = { isLoginOngoing = false }
                    )
                }
            }
        }

        if (isLoginOngoing) {
            loginScreen.content()
        } else {
            Text("Main screen")
        }
    }
}