package com.daniebeler.pfpixelix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import io.ktor.http.*

class LoginActivity : ComponentActivity() {
    private val appComponent = MyApplication.appComponent
    private val loginScreen: LoginScreen = appComponent.createLoginScreen()

    override fun onCreate(savedInstanceState: Bundle?) {
        (appComponent.contextNavigation as AndroidContextNavigation).context = this
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            loginScreen.content()
        }
    }

    override fun onStart() {
        super.onStart()

        loginScreen.onStart(
            baseUrl = intent.extras?.getString("base_url"),
            accessToken = intent.extras?.getString("access_token"),
            url = intent.data?.let { Url(it.toString()) }
        )
    }
}

