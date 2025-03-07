package com.daniebeler.pfpixelix.ui.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.daniebeler.pfpixelix.MyApplication
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.AMOLED
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.DARK
import com.daniebeler.pfpixelix.domain.model.AppThemeMode.LIGHT

@Composable
actual fun generateColorScheme(
    nightModeValue: Int,
    dynamicColor: Boolean,
    lightScheme: ColorScheme,
    darkScheme: ColorScheme
): ColorScheme {
    val context = LocalAppComponent.current.context
    return remember(
        nightModeValue, dynamicColor, lightScheme, darkScheme
    ) {
        if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            when (nightModeValue) {
                AMOLED -> dynamicDarkColorScheme(context).toAmoled()
                DARK -> dynamicDarkColorScheme(context)
                else -> dynamicLightColorScheme(context)
            }
        } else {
            when (nightModeValue) {
                AMOLED -> darkScheme.toAmoled()
                DARK -> darkScheme
                else -> lightScheme
            }
        }
    }
}

actual fun applySystemNightMode(mode: Int) {
    AppCompatDelegate.setDefaultNightMode(
        when (mode) {
            LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            AMOLED, DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    )
    val activity = MyApplication.currentActivity?.get()
    activity?.enableEdgeToEdge(
        when (mode) {
            LIGHT -> SystemBarStyle.light(
                Color.Transparent.toArgb(), Color.Transparent.toArgb()
            )

            AMOLED, DARK -> SystemBarStyle.dark(
                Color.Transparent.toArgb()
            )

            else -> SystemBarStyle.dark(
                Color.Transparent.toArgb()
            )
        }
    )
}