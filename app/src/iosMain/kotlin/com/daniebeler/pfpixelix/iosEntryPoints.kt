package com.daniebeler.pfpixelix

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.create
import com.daniebeler.pfpixelix.ui.composables.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.LoginComposable
import com.daniebeler.pfpixelix.utils.IosContext
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import platform.UIKit.UIViewController

fun LoginViewController(): UIViewController = ComposeUIViewController {

    val mainComponent = AppComponent.create(IosContext)
    CompositionLocalProvider(
        LocalKmpContext provides IosContext,
        LocalAppComponent provides mainComponent
    ) {
        LoginComposable(false, "")
    }
}