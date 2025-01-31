package com.daniebeler.pfpixelix.di

import com.daniebeler.pfpixelix.ui.composables.LoginViewModel
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate

@Component
abstract class KmpViewModelComponent(
    @Component val app: AppComponent
) {
    abstract val loginViewModel: LoginViewModel
    companion object
}

@KmpComponentCreate
expect fun KmpViewModelComponent.Companion.create(app: AppComponent): KmpViewModelComponent