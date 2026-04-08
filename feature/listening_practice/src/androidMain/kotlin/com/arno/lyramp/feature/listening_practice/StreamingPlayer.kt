package com.arno.lyramp.feature.listening_practice

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(UnstableApi::class)
actual class StreamingPlayer actual constructor() : KoinComponent {
        private val context: Context by inject()

        private var exoPlayer: ExoPlayer? = null
        private var positionUpdateJob: Job? = null
        private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        private val _currentPositionMs = MutableStateFlow(0L)
        actual val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

        private val _durationMs = MutableStateFlow(0L)
        actual val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

        private val _isPlaying = MutableStateFlow(false)
        actual val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

        private val _isReady = MutableStateFlow(false)
        actual val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

        actual suspend fun prepare(url: String) {
                withContext(Dispatchers.Main) {
                        try {
                                cleanAll()

                                exoPlayer = ExoPlayer.Builder(context).build().apply {
                                        addListener(object : Player.Listener {
                                                override fun onPlaybackStateChanged(playbackState: Int) {
                                                        when (playbackState) {
                                                                Player.STATE_READY -> {
                                                                        _durationMs.value = duration
                                                                        _isReady.value = true
                                                                }

                                                                Player.STATE_ENDED -> {
                                                                        _isPlaying.value = false
                                                                        stopPositionUpdates()
                                                                }

                                                                Player.STATE_BUFFERING -> {
                                                                        Log.d(TAG, "  buffering")
                                                                }

                                                                Player.STATE_IDLE -> {
                                                                        Log.d(TAG, "idle")
                                                                }
                                                        }
                                                }

                                                override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                                                        _isPlaying.value = isPlayingNow
                                                        if (isPlayingNow) startPositionUpdates()
                                                        else stopPositionUpdates()
                                                }

                                                override fun onPlayerError(error: PlaybackException) {
                                                        Log.e(TAG, "ExoPlayer error: ${error.message}", error)
                                                        _isReady.value = false
                                                        _isPlaying.value = false
                                                }
                                        })
                                }

                                val dataSourceFactory = DefaultHttpDataSource.Factory()
                                val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                                        .createMediaSource(MediaItem.fromUri(url))

                                exoPlayer?.apply {
                                        setMediaSource(mediaSource)
                                        prepare()
                                }

                        } catch (e: Exception) {
                                Log.e(TAG, "ExoPlayer: ${e.message}", e)
                                _isReady.value = false
                        }
                }
        }

        actual fun play() {
                try {
                        exoPlayer?.let { player ->
                                if (!player.isPlaying) player.play()
                        }
                } catch (e: Exception) {
                        Log.e(TAG, "play: ${e.message}")
                }
        }

        actual fun pause() {
                try {
                        exoPlayer?.let { player ->
                                if (player.isPlaying) player.pause()
                        }
                } catch (e: Exception) {
                        Log.e(TAG, "Error pause: ${e.message}")
                }
        }

        actual fun seekTo(positionMs: Long) {
                try {
                        exoPlayer?.let { player ->
                                val safePosition = positionMs.coerceIn(0, _durationMs.value)
                                player.seekTo(safePosition)
                                _currentPositionMs.value = safePosition
                        }
                } catch (e: Exception) {
                        Log.e(TAG, "seek: ${e.message}")
                }
        }

        actual fun rewind(milliseconds: Long) {
                val newPosition = (_currentPositionMs.value - milliseconds).coerceAtLeast(0)
                seekTo(newPosition)
        }

        actual fun setPlaybackSpeed(speed: Float) {
                try {
                        exoPlayer?.setPlaybackSpeed(speed)
                } catch (e: Exception) {
                        Log.e(TAG, "setPlaybackSpeed: ${e.message}")
                }
        }

        actual fun release() {
                cleanAll()
                scope.cancel()
        }

        private fun cleanAll() {
                stopPositionUpdates()
                try {
                        exoPlayer?.apply {
                                stop()
                                release()
                        }
                        exoPlayer = null
                        _isPlaying.value = false
                        _isReady.value = false
                        _currentPositionMs.value = 0
                        _durationMs.value = 0
                } catch (e: Exception) {
                        Log.e(TAG, "release: ${e.message}")
                }
        }

        private fun startPositionUpdates() {
                stopPositionUpdates()
                positionUpdateJob = scope.launch {
                        while (isActive) {
                                try {
                                        exoPlayer?.let { player ->
                                                if (player.isPlaying) _currentPositionMs.value = player.currentPosition
                                        }
                                } catch (e: Exception) {
                                        Log.e(TAG, "pos update: ${e.message}")
                                }
                                delay(100)
                        }
                }
        }

        private fun stopPositionUpdates() {
                positionUpdateJob?.cancel()
                positionUpdateJob = null
        }

        companion object {
                private const val TAG = "StreamingPlayer.ExoPlayer"
        }
}
