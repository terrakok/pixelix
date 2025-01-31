package com.daniebeler.pfpixelix.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.daniebeler.pfpixelix.MyApplication
import com.daniebeler.pfpixelix.di.ViewModelComponent
import com.daniebeler.pfpixelix.di.create

@Composable
internal fun <T> injectViewModel(key: String, block: ViewModelComponent.() -> T): T = remember(key) {
    ViewModelComponent::class.create(MyApplication.appComponent).block()
}