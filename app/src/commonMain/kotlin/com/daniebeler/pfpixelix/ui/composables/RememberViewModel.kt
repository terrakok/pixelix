package com.daniebeler.pfpixelix.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.KmpViewModelComponent
import com.daniebeler.pfpixelix.di.create

val LocalAppComponent = staticCompositionLocalOf<AppComponent> { error("no AppComponent") }

@Composable
internal fun <T> rememberViewModel(key: String, block: KmpViewModelComponent.() -> T): T {
    val app = LocalAppComponent.current
    return remember(key) {
        KmpViewModelComponent.create(app).block()
    }
}