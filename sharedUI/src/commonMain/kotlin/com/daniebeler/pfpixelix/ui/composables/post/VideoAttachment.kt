package com.daniebeler.pfpixelix.ui.composables.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.ui.composables.widgets.LocalVideoPlaybackCoordinator
import com.daniebeler.pfpixelix.utils.KeepScreenOn
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.vectorResource
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.PlaybackEvent
import org.openani.mediamp.PlaybackException
import org.openani.mediamp.compose.MediampPlayerSurface
import org.openani.mediamp.compose.rememberMediampPlayer
import org.openani.mediamp.features.AudioLevelController
import org.openani.mediamp.features.audioTracks
import org.openani.mediamp.playUri
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.autoplay
import pixelix.app.generated.resources.close
import pixelix.app.generated.resources.pause
import pixelix.app.generated.resources.volume_loud
import pixelix.app.generated.resources.volume_mute

@Composable
fun VideoPlayerContent(
    id: String,
    url: String,
    aspectRatio: Float?,
    volumeOn: Boolean,
    onToggleVolume: () -> Unit,
    autoplay: Boolean,
    onReady: () -> Unit = {},
    allowFullscreenOnClick: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val coordinator = LocalVideoPlaybackCoordinator.current
    val isActive = coordinator.activeId == id
    val player = rememberMediampPlayer()
    val state by player.state.collectAsState()
    val progress by player.playbackProgress.collectAsState(0f)
    val audioTracks = player.audioTracks
    val tracks by (audioTracks?.candidates ?: flowOf(emptyList()))
        .collectAsState(emptyList())
    // Some backends cannot report tracks; still allow muting on those platforms.
    val hasAudio = audioTracks == null || tracks.isNotEmpty()
    var isFullscreen by remember(url) { mutableStateOf(false) }
    var videoFrameIsVisible by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val isResumed = lifecycleState == Lifecycle.State.RESUMED
    val shouldAutoplay = isActive && autoplay && isResumed
    val latestShouldAutoplay by rememberUpdatedState(shouldAutoplay)
    val latestOnReady by rememberUpdatedState(onReady)

    // Subscribe before opening: end events are not replayed by Mediamp.
    LaunchedEffect(player, id, coordinator, lifecycleOwner) {
        player.events.filterIsInstance<PlaybackEvent.MediaEnded>().collect {
            if (coordinator.activeId == id &&
                lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED
            ) {
                player.play()
            }
        }
    }
    LaunchedEffect(player, url) {
        try {
            player.playUri(url, playWhenReady = latestShouldAutoplay)
        } catch (error: PlaybackException) {
            Logger.e(error) { "Unable to open video: $url" }
        }
    }
    DisposableEffect(id, coordinator) {
        onDispose { coordinator.clear(id) }
    }
    LaunchedEffect(player, state.isPlaying) {
        if (state.isPlaying) latestOnReady()
    }
    LaunchedEffect(player, isActive, volumeOn) {
        player.features[AudioLevelController]?.setMute(!isActive || !volumeOn)
    }
    LaunchedEffect(player, shouldAutoplay) {
        if (shouldAutoplay) player.play() else player.pause()
    }
    LaunchedEffect(player, isActive, isResumed) {
        if (!isActive || !isResumed) player.pause()
    }

    if (state.isPlaying) KeepScreenOn()

    val togglePlayback = {
        if (player.state.value.playWhenReady) {
            player.pause()
        } else {
            coordinator.requestActive(id)
            player.play()
        }
    }
    Column {
        Box(
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(aspectRatio ?: 16f / 9f)
                .isVisible(threshold = 50) { visible ->
                    videoFrameIsVisible = visible
                    coordinator.setVisible(id, visible || isFullscreen)
                }
                .then(if (allowFullscreenOnClick) Modifier.clickable {
                    coordinator.requestActive(id)
                    isFullscreen = true
                } else Modifier)
        ) {
            // Only one native surface is attached to the player at a time.
            if (!isFullscreen) {
                MediampPlayerSurface(player, Modifier.fillMaxSize())
                Column(Modifier.align(Alignment.BottomEnd)) {
                    if (hasAudio) VolumeButton(volumeOn, onToggleVolume)
                    PlaybackButton(state.playWhenReady, togglePlayback)
                }
            }
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.background
        )
    }

    LaunchedEffect(isFullscreen) {
        coordinator.setVisible(id, videoFrameIsVisible || isFullscreen)
    }
    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            NavigationBackHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                isBackEnabled = true,
                onBackCompleted = { isFullscreen = false }
            )
            var controlsVisible by remember { mutableStateOf(true) }
            Box(
                Modifier.fillMaxSize().background(Color.Black).clickable {
                    controlsVisible = !controlsVisible
                }
            ) {
                MediampPlayerSurface(player, Modifier.fillMaxSize())
                if (controlsVisible) {
                    IconButton(
                        onClick = { isFullscreen = false },
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors()
                    ) {
                        Icon(vectorResource(Res.drawable.close), "Exit Fullscreen")
                    }
                    Row(
                        Modifier.align(Alignment.BottomCenter),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlaybackButton(state.playWhenReady, togglePlayback)
                        Box(Modifier.weight(1f)) { TimelineControls(player) }
                        if (hasAudio) VolumeButton(volumeOn, onToggleVolume)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaybackButton(playWhenReady: Boolean, onClick: () -> Unit) {
    IconButton(
        modifier = Modifier.padding(8.dp),
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors()
    ) {
        Icon(
            vectorResource(if (playWhenReady) Res.drawable.pause else Res.drawable.autoplay),
            contentDescription = if (playWhenReady) "Pause" else "Play",
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun VolumeButton(volumeOn: Boolean, onClick: () -> Unit) {
    IconButton(
        modifier = Modifier.padding(8.dp),
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors()
    ) {
        Icon(
            vectorResource(if (volumeOn) Res.drawable.volume_loud else Res.drawable.volume_mute),
            contentDescription = if (volumeOn) "Volume on" else "Volume off",
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun TimelineControls(playerState: MediampPlayer) {
    val positionMillis by playerState.currentPositionMillis.collectAsState()
    val properties by playerState.mediaProperties.collectAsState()
    val durationMillis = properties?.durationMillis?.takeIf { it > 0 }
    var dragProgress by remember(playerState) { mutableStateOf<Float?>(null) }
    val progress = durationMillis?.let {
        (positionMillis.toDouble() / it).toFloat().coerceIn(0f, 1f)
    } ?: 0f
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Slider(
            modifier = Modifier.weight(1f),
            value = dragProgress ?: progress,
            enabled = durationMillis != null,
            onValueChange = { dragProgress = it },
            onValueChangeFinished = {
                val target = dragProgress
                if (target != null && durationMillis != null) {
                    playerState.seekTo((target * durationMillis).toLong())
                }
                dragProgress = null
            },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
            )
        )
        val remainingText = durationMillis?.let {
            val displayedPosition = dragProgress?.let { fraction -> (fraction * it).toLong() }
                ?: positionMillis
            val seconds = ((it - displayedPosition) / 1000).coerceAtLeast(0)
            "${(seconds / 60).toString().padStart(2, '0')}:${(seconds % 60).toString().padStart(2, '0')}"
        } ?: "--:--"
        Text(
            text = remainingText,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
