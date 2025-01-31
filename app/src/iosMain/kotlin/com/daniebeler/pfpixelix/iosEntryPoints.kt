package com.daniebeler.pfpixelix

import androidx.compose.ui.window.ComposeUIViewController
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.create
import com.daniebeler.pfpixelix.utils.IosContext
import platform.UIKit.UIViewController

fun LoginViewController(): UIViewController {
    val mainComponent = AppComponent.create(IosContext)
    val loginScreen = mainComponent.loginScreen
    return ComposeUIViewController {
        loginScreen.content()
    }
}