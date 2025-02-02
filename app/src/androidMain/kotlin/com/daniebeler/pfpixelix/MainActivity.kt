package com.daniebeler.pfpixelix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    companion object {
        const val KEY_DESTINATION: String = "destination"
        const val KEY_DESTINATION_PARAM: String = "destination_parameter"

        enum class StartNavigation {
            Notifications, Profile, Post
        }
    }
    private val appComponent = MyApplication.appComponent
    private val mainScreen = appComponent.createMainScreen()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        (appComponent.contextNavigation as AndroidContextNavigation).context = this
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        mainScreen.onCreate()

        setContent {
            mainScreen.content(intent.extras?.getString(KEY_DESTINATION) ?: "")
        }
    }
}
