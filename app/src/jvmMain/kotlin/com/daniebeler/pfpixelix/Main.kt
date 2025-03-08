package com.daniebeler.pfpixelix

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import coil3.SingletonImageLoader
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.create
import com.daniebeler.pfpixelix.domain.service.file.DesktopFileService
import com.daniebeler.pfpixelix.domain.service.icon.DesktopAppIconManager
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.configureJavaLogger
import com.daniebeler.pfpixelix.utils.configureLogger
import java.awt.Desktop
import java.awt.Dimension

fun main() {
    //https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-desktop-swing-interoperability.html
    System.setProperty("compose.swing.render.on.graphics", "true")
    System.setProperty("compose.interop.blending", "true")
    application {
        val appComponent = AppComponent.Companion.create(
            object : KmpContext() {},
            DesktopFileService(),
            DesktopAppIconManager()
        )

        configureJavaLogger()

        SingletonImageLoader.setSafe {
            appComponent.provideImageLoader()
        }

        Desktop.getDesktop().setOpenURIHandler { url ->
            appComponent.systemUrlHandler.onRedirect(
                url.uri.toString()
            )
        }

        Window(
            title = "Pixelix",
            state = rememberWindowState(width = 600.dp, height = 1000.dp),
            onCloseRequest = ::exitApplication,
        ) {
            window.minimumSize = Dimension(400, 600)
            App(appComponent) { exitApplication() }
        }
    }
}