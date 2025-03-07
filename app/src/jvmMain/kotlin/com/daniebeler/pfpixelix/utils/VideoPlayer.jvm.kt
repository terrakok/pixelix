package com.daniebeler.pfpixelix.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.factory.discovery.strategy.OsxNativeDiscoveryStrategy
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.InputEvents
import java.awt.Component
import java.util.Locale

actual class VideoPlayer actual constructor(
    context: KmpContext,
    private val coroutineScope: CoroutineScope
) {
    private val mpComponent = initializeMediaPlayerComponent()
    private val player = mpComponent.mediaPlayer()

    actual var progress: ((current: Long, duration: Long) -> Unit)? = null
    actual var hasAudio: ((Boolean) -> Unit)? = null

    private val listener = object : MediaPlayerEventAdapter() {
        override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
            hasAudio?.invoke(player.audio().trackCount() > 0)
        }

        override fun positionChanged(mediaPlayer: MediaPlayer?, newPosition: Float) {
            val status = player.status()
            progress?.invoke((status.length() * status.position()).toLong(), status.length())
        }
    }

    init {
        player.events().addMediaPlayerEventListener(listener)
    }

    @Composable
    actual fun view(modifier: Modifier) {
        SwingPanel(
            factory = { mpComponent },
            background = Color.Transparent,
            modifier = modifier
        )
    }

    actual fun prepare(url: String) {
        player.media().prepare(url)
    }

    actual fun play() {
        player.controls().play()
    }

    actual fun pause() {
        player.controls().pause()
    }

    actual fun release() {
        player.events().removeMediaPlayerEventListener(listener)
        player.release()
    }

    actual fun audio(enable: Boolean) {
        player.audio().isMute = !enable
    }

    private fun Component.mediaPlayer() = when (this) {
        is CallbackMediaPlayerComponent -> mediaPlayer()
        is EmbeddedMediaPlayerComponent -> mediaPlayer()
        else -> error("mediaPlayer() can only be called on vlcj player components")
    }

    private fun initializeMediaPlayerComponent(): Component {
        NativeDiscovery(OsxNativeDiscoveryStrategy()).discover()
        return if (isMacOS()) {
            CallbackMediaPlayerComponent(null, null, InputEvents.NONE, true, null)
        } else {
            EmbeddedMediaPlayerComponent(null, null, null, InputEvents.NONE, null)
        }
    }

    private fun isMacOS(): Boolean {
        val os = System.getProperty("os.name", "generic").lowercase(Locale.ENGLISH)
        return "mac" in os || "darwin" in os
    }
}