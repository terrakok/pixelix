package com.daniebeler.pfpixelix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.composables.ThemeViewModel
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40, secondary = PurpleGrey40, tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun PixelixTheme(
    darkThemeSystem: Boolean = isSystemInDarkTheme(),
    viewModel: ThemeViewModel = rememberViewModel(key = "Theme") { themeViewModel },
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme: Boolean = when (viewModel.currentTheme.theme) {
        "dark" -> {
            true
        }
        "light" -> {
            false
        }
        else -> {
            darkThemeSystem
        }
    }
    val colorScheme = when {
        //todo
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, content = content
    )

}