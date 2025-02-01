package com.daniebeler.pfpixelix

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.create
import com.daniebeler.pfpixelix.utils.ContextNavigation
import com.daniebeler.pfpixelix.utils.IosContext
import io.ktor.http.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import platform.Foundation.NSURL.Companion.URLWithString
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

class ExternalUrlHandler {
    private val urlFlow = MutableSharedFlow<String>()
    val urls = urlFlow.asSharedFlow()

    @OptIn(DelicateCoroutinesApi::class)
    fun onExternalUrl(url: String) {
        GlobalScope.launch { urlFlow.emit(url) }
    }
}

fun ComposeAppViewController(externalUrlHandler: ExternalUrlHandler): UIViewController {
    var mainComponent: AppComponent? = null
    var mainScreen: MainScreen? by mutableStateOf(null)
    var loginScreen: LoginScreen? by mutableStateOf(null)

    val contextNavigation = object : ContextNavigation {
        override fun updateAuthToV2(baseUrl: String, accessToken: String) {
            Logger.d { "updateAuthToV2(baseUrl=$baseUrl, accessToken)" }
            mainScreen = null
            loginScreen = mainComponent!!.createLoginScreen().also {
                it.onStart(baseUrl, accessToken, null)
            }
        }

        override fun gotoLoginActivity(isAbleToGotBack: Boolean) {
            Logger.d { "gotoLoginActivity(isAbleToGotBack=$isAbleToGotBack)" }
            if (!isAbleToGotBack) {
                mainScreen = null
            }
            loginScreen = mainComponent!!.createLoginScreen()
        }

        override fun redirect() {
            Logger.d { "redirect()" }
            loginScreen = null
            mainScreen = mainComponent!!.createMainScreen().also { it.onCreate() }
        }

        override fun openUrlInApp(url: String) {
            Logger.d { "openUrlInApp(url=$url)" }
            try {
                UIApplication.sharedApplication.openURL(URLWithString(url)!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    mainComponent = AppComponent.create(IosContext, contextNavigation)
    mainScreen = mainComponent.createMainScreen().also { it.onCreate() }

    return ComposeUIViewController {
        LaunchedEffect(Unit) {
            externalUrlHandler.urls.collect { url ->
                Logger.d { "collect url: $url" }
                loginScreen?.onStart(
                    baseUrl = null,
                    accessToken = null,
                    url = Url(url)
                )
            }
        }

        mainScreen?.content("") //todo
        loginScreen?.content()
    }
}