package com.arno.lyramp.feature.listening_practice.playback

import com.arno.lyramp.feature.listening_practice.StreamingPlayer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

internal class LinePlaybackController {
        private val player = StreamingPlayer()

        private val _isSlowMode = MutableStateFlow(false)
        private val _currentLineIsPlaying = MutableStateFlow(false)

        private var linePlaybackJob: Job? = null

        val isPlaying: StateFlow<Boolean> get() = player.isPlaying
        val currentPositionMs: StateFlow<Long> get() = player.currentPositionMs
        val durationMs: StateFlow<Long> get() = player.durationMs
        val isReady: StateFlow<Boolean> get() = player.isReady
        val currentLineIsPlaying: StateFlow<Boolean> = _currentLineIsPlaying.asStateFlow()
        val isSlowMode: StateFlow<Boolean> = _isSlowMode.asStateFlow()

        suspend fun prepare(url: String) {
                player.prepare(url)
                applySpeed()
        }

        fun pause() = player.pause()

        fun playPause() {
                if (player.isPlaying.value) player.pause() else player.play()
        }

        fun seekTo(ms: Long) = player.seekTo(ms)
        fun rewind(ms: Long) = player.rewind(ms)

        fun forward(ms: Long) {
                val newPosition = (player.currentPositionMs.value + ms).coerceAtMost(player.durationMs.value)
                player.seekTo(newPosition)
        }

        fun toggleSlowMode(): Boolean {
                _isSlowMode.value = !_isSlowMode.value
                applySpeed()
                return _isSlowMode.value
        }

        fun setSlowMode(slow: Boolean) {
                _isSlowMode.value = slow
                applySpeed()
        }

        private fun applySpeed() {
                player.setPlaybackSpeed(if (_isSlowMode.value) SLOW_SPEED else NORMAL_SPEED)
        }

        fun toggleSegment(scope: CoroutineScope, startMs: Long, endMs: Long) {
                if (_currentLineIsPlaying.value) {
                        stopSegment()
                        return
                }
                linePlaybackJob?.cancel()
                linePlaybackJob = scope.launch {
                        val playStartMs = (startMs - LINE_PLAYBACK_PADDING_MS).coerceAtLeast(0)
                        val playEndMs = endMs + LINE_PLAYBACK_PADDING_MS
                        try {
                                player.pause()
                                _currentLineIsPlaying.value = false
                                player.seekTo(playStartMs)
                                waitForSeek(playStartMs)
                                player.play()
                                _currentLineIsPlaying.value = true
                                waitForSegmentEnd(playStartMs, playEndMs)
                        } catch (ce: CancellationException) {
                                throw ce
                        } finally {
                                player.pause()
                                _currentLineIsPlaying.value = false
                        }
                }
        }

        fun stopSegment() {
                linePlaybackJob?.cancel()
                linePlaybackJob = null
                player.pause()
                _currentLineIsPlaying.value = false
        }

        fun release() {
                linePlaybackJob?.cancel()
                linePlaybackJob = null
                _currentLineIsPlaying.value = false
                player.release()
        }

        private suspend fun waitForSeek(positionMs: Long) {
                withTimeoutOrNull(SEEK_TIMEOUT_MS) {
                        while (abs(player.currentPositionMs.value - positionMs) > SEEK_TOLERANCE_MS) {
                                delay(SEEK_POLL_INTERVAL_MS)
                        }
                }
        }

        private suspend fun waitForSegmentEnd(startMs: Long, endMs: Long) {
                val timeoutMs = ((endMs - startMs) * SEGMENT_TIMEOUT_MULTIPLIER + SEGMENT_TIMEOUT_PADDING_MS)
                        .coerceAtLeast(MIN_SEGMENT_TIMEOUT_MS)
                withTimeoutOrNull(timeoutMs) {
                        while (player.currentPositionMs.value < endMs) {
                                delay(POSITION_POLL_INTERVAL_MS)
                        }
                }
        }

        private companion object {
                const val NORMAL_SPEED = 1.0f
                const val SLOW_SPEED = 0.7f

                const val LINE_PLAYBACK_PADDING_MS = 120L
                const val SEEK_TIMEOUT_MS = 1_500L
                const val SEEK_TOLERANCE_MS = 180L
                const val SEEK_POLL_INTERVAL_MS = 16L
                const val POSITION_POLL_INTERVAL_MS = 30L
                const val SEGMENT_TIMEOUT_MULTIPLIER = 2L
                const val SEGMENT_TIMEOUT_PADDING_MS = 3_000L
                const val MIN_SEGMENT_TIMEOUT_MS = 1_500L
        }
}
