package com.daniebeler.pfpixelix.di

import com.daniebeler.pfpixelix.ui.composables.LoginViewModel
import com.daniebeler.pfpixelix.ui.composables.ThemeViewModel
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate

@Component
abstract class KmpViewModelComponent(
    @Component val app: AppComponent
) {
    abstract val loginViewModel: LoginViewModel
    abstract val themeViewModel: ThemeViewModel
    companion object
}

@KmpComponentCreate
expect fun KmpViewModelComponent.Companion.create(app: AppComponent): KmpViewModelComponent