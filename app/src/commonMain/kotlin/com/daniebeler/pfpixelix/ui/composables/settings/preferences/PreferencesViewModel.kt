package com.daniebeler.pfpixelix.ui.composables.settings.preferences

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.usecase.*
import com.daniebeler.pfpixelix.utils.KmpContext
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class PreferencesViewModel @Inject constructor(
    private val storeHideSensitiveContentUseCase: StoreHideSensitiveContentUseCase,
    private val getHideSensitiveContentUseCase: GetHideSensitiveContentUseCase,
    private val storeHideAltTextButtonUseCase: StoreHideAltTextButtonUseCase,
    private val getHideAltTextButtonUseCase: GetHideAltTextButtonUseCase,
    private val getUseInAppBrowserUseCase: GetUseInAppBrowserUseCase,
    private val storeUseInAppBrowserUseCase: StoreUseInAppBrowserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getOwnInstanceDomainUseCase: GetOwnInstanceDomainUseCase,
    private val openExternalUrlUseCase: OpenExternalUrlUseCase,
    private val getActiveAppIconUseCase: GetActiveAppIconUseCase
) : ViewModel() {

    var isSensitiveContentHidden by mutableStateOf(true)
    var isAltTextButtonHidden by mutableStateOf(false)
    var isUsingInAppBrowser by mutableStateOf(true)
    var appIcon by mutableStateOf<ImageBitmap?>(null)

    var cacheSize by mutableStateOf("")

    var versionName by mutableStateOf("")

    init {
        viewModelScope.launch {
            getHideSensitiveContentUseCase().collect { res ->
                isSensitiveContentHidden = res
            }
        }

        viewModelScope.launch {
            getHideAltTextButtonUseCase().collect { res ->
                isAltTextButtonHidden = res
            }
        }

        viewModelScope.launch {
            getUseInAppBrowserUseCase().collect { res ->
                isUsingInAppBrowser = res
            }
        }
    }

    fun getAppIcon(context: KmpContext) {
        appIcon = getActiveAppIconUseCase(context)
    }

    fun getVersionName(context: KmpContext) {
        //todo
//        try {
//            versionName =
//                KmpContext.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun storeHideSensitiveContent(value: Boolean) {
        isSensitiveContentHidden = value
        viewModelScope.launch {
            storeHideSensitiveContentUseCase(value)
        }
    }

    fun storeHideAltTextButton(value: Boolean) {
        isAltTextButtonHidden = value
        viewModelScope.launch {
            storeHideAltTextButtonUseCase(value)
        }
    }

    fun storeUseInAppBrowser(value: Boolean) {
        isUsingInAppBrowser = value
        viewModelScope.launch {
            storeUseInAppBrowserUseCase(value)
        }
    }

    fun openMoreSettingsPage(context: KmpContext) {
        viewModelScope.launch {
            val domain = getOwnInstanceDomainUseCase()
            val moreSettingUrl = "https://$domain/settings/home"
            openExternalUrlUseCase(context, moreSettingUrl)
        }
    }
}