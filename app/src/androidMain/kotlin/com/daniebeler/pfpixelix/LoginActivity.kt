package com.daniebeler.pfpixelix

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import io.ktor.http.Url

class LoginActivity : ComponentActivity() {
    private val loginScreen: LoginScreen =
        MyApplication.appComponent.appComponent.loginScreen

    override fun onCreate(savedInstanceState: Bundle?) {
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
            url = intent.data?.let { Url(it.toString()) },
            ::redirect
        )
    }

    private fun redirect() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        applicationContext.startActivity(intent)
    }
}

