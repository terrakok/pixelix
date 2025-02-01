package com.daniebeler.pfpixelix

import androidx.compose.runtime.*
import com.daniebeler.pfpixelix.common.Resource
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.HostSelectionInterceptorInterface
import com.daniebeler.pfpixelix.domain.model.LoginData
import com.daniebeler.pfpixelix.domain.usecase.*
import com.daniebeler.pfpixelix.ui.composables.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.LoginComposable
import com.daniebeler.pfpixelix.ui.theme.PixelixTheme
import com.daniebeler.pfpixelix.utils.ContextNavigation
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class LoginScreen(
    private val appComponent: AppComponent,
    private val contextNavigation: ContextNavigation,
    private val obtainTokenUseCase: ObtainTokenUseCase,
    private val verifyTokenUseCase: VerifyTokenUseCase,
    private val updateLoginDataUseCase: UpdateLoginDataUseCase,
    private val finishLoginUseCase: FinishLoginUseCase,
    private val newLoginDataUseCase: AddNewLoginUseCase,
    private val getOngoingLoginUseCase: GetOngoingLoginUseCase,
    private val hostSelectionInterceptorInterface: HostSelectionInterceptorInterface
) {
    private var isLoadingAfterRedirect: Boolean by mutableStateOf(false)
    private var error: String by mutableStateOf("")

    @Composable
    fun content() {
        CompositionLocalProvider(
            LocalKmpContext provides appComponent.context,
            LocalAppComponent provides appComponent
        ) {
            PixelixTheme {
                LoginComposable(isLoadingAfterRedirect, error)
            }
        }
    }

    fun onStart(
        baseUrl: String?,
        accessToken: String?,
        url: Url?
    ) {
        if (baseUrl != null && accessToken != null) {
            hostSelectionInterceptorInterface.setHost(baseUrl)
            hostSelectionInterceptorInterface.setToken(accessToken)
            CoroutineScope(Dispatchers.Default).launch {
                verifyToken(LoginData(baseUrl = baseUrl, accessToken = accessToken), true)
            }
        }

        //Check if the activity was started after the authentication
        if (url == null || !url.toString().startsWith("pixelix-android-auth://callback")) return

        val code = url.parameters["code"] ?: ""

        if (code.isNotEmpty()) {

            isLoadingAfterRedirect = true
            CoroutineScope(Dispatchers.Default).launch {
                getTokenAndRedirect(code)
            }
        }
    }

    private suspend fun getTokenAndRedirect(code: String) {
        val loginData: LoginData? = getOngoingLoginUseCase()
        if (loginData == null) {
            error = "an unexpected error occured"
            isLoadingAfterRedirect = false
        } else {
            obtainTokenUseCase(loginData.clientId, loginData.clientSecret, code).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val newLoginData = loginData.copy(accessToken = result.data!!.accessToken)
                        updateLoginDataUseCase(newLoginData)
                        verifyToken(newLoginData, false)
                    }

                    is Resource.Error -> {
                        error = result.message ?: "Error"
                        isLoadingAfterRedirect = false
                    }

                    is Resource.Loading -> {
                        isLoadingAfterRedirect = true
                    }
                }
            }
        }
    }

    private suspend fun verifyToken(
        loginData: LoginData,
        updateToAuthV2: Boolean
    ) {
        verifyTokenUseCase(loginData.accessToken).collect { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data == null) {
                        error = "an unexpected error occured"
                        isLoadingAfterRedirect = false
                        return@collect
                    }
                    val newLoginData = loginData.copy(
                        accountId = result.data.id,
                        username = result.data.username,
                        avatar = result.data.avatar,
                        displayName = result.data.displayname,
                        followers = result.data.followersCount,
                        loginOngoing = false
                    )
                    if (updateToAuthV2) {
                        newLoginDataUseCase.invoke(newLoginData)
                    }
                    finishLoginUseCase(newLoginData, newLoginData.accountId)

                    contextNavigation.redirect()
                }

                is Resource.Error -> {
                    error = result.message ?: "Error"
                    isLoadingAfterRedirect = false
                }

                is Resource.Loading -> {
                    isLoadingAfterRedirect = true
                }
            }
        }
    }
}