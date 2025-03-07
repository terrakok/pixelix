package com.daniebeler.pfpixelix.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.video_player_not_supported

actual class VideoPlayer actual constructor(
    context: KmpContext,
    private val coroutineScope: CoroutineScope
) {
    actual var progress: ((current: Long, duration: Long) -> Unit)? = null
    actual var hasAudio: ((Boolean) -> Unit)? = null

    @Composable
    actual fun view(modifier: Modifier) {
        Box(
            modifier.background(MaterialTheme.colors.secondaryVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(Res.string.video_player_not_supported))
        }
    }

    actual fun prepare(url: String) {}

    actual fun play() {}
    actual fun pause() {}
    actual fun release() {}
    actual fun audio(enable: Boolean) {}
}