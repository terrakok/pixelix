package com.daniebeler.pfpixelix

import androidx.compose.ui.window.ComposeUIViewController
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.create
import com.daniebeler.pfpixelix.utils.IosContext
import platform.UIKit.UIViewController


private val mainComponent by lazy { AppComponent.create(IosContext) }

fun LoginViewController(): UIViewController {
    val loginScreen = mainComponent.loginScreen

//    loginScreen.onStart()
    return ComposeUIViewController {
        loginScreen.content()
    }
}