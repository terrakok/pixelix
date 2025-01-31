package com.daniebeler.pfpixelix

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.daniebeler.pfpixelix.common.Resource
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.HostSelectionInterceptorInterface
import com.daniebeler.pfpixelix.domain.model.LoginData
import com.daniebeler.pfpixelix.domain.usecase.AddNewLoginUseCase
import com.daniebeler.pfpixelix.domain.usecase.FinishLoginUseCase
import com.daniebeler.pfpixelix.domain.usecase.GetOngoingLoginUseCase
import com.daniebeler.pfpixelix.domain.usecase.ObtainTokenUseCase
import com.daniebeler.pfpixelix.domain.usecase.UpdateLoginDataUseCase
import com.daniebeler.pfpixelix.domain.usecase.VerifyTokenUseCase
import com.daniebeler.pfpixelix.ui.composables.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.LoginComposable
import com.daniebeler.pfpixelix.ui.theme.PixelixTheme
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class LoginScreen(
    private val appComponent: AppComponent,
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
                Scaffold { paddingValues ->
                    Column(Modifier.padding(paddingValues)) {

                    }
                    LoginComposable(isLoadingAfterRedirect, error)
                }
            }
        }
    }

    fun onStart(
        baseUrl: String?,
        accessToken: String?,
        url: Url?,
        redirect: () -> Unit
    ) {
        if (baseUrl != null && accessToken != null) {
            hostSelectionInterceptorInterface.setHost(baseUrl)
            hostSelectionInterceptorInterface.setToken(accessToken)
            CoroutineScope(Dispatchers.Default).launch {
                verifyToken(LoginData(baseUrl = baseUrl, accessToken = accessToken), true, redirect)
            }
        }

        //Check if the activity was started after the authentication
        if (url == null || !url.toString().startsWith("pixelix-android-auth://callback")) return

        val code = url.parameters["code"] ?: ""

        if (code.isNotEmpty()) {

            isLoadingAfterRedirect = true
            CoroutineScope(Dispatchers.Default).launch {
                getTokenAndRedirect(code, redirect)
            }
        }
    }

    private suspend fun getTokenAndRedirect(
        code: String,
        redirect: () -> Unit
    ) {
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
                        verifyToken(newLoginData, false, redirect)
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
        updateToAuthV2: Boolean,
        redirect: () -> Unit
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

                    redirect()
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