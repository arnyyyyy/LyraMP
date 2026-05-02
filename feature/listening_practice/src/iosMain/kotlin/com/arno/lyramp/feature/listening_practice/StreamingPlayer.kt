package com.arno.lyramp.feature.listening_practice

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerItemStatusFailed
import platform.AVFoundation.AVPlayerItemStatusReadyToPlay
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.seekToTime
import platform.AVFoundation.setRate
import platform.AVFoundation.timeControlStatus
import platform.CoreMedia.CMTimeMake
import platform.CoreMedia.CMTimeGetSeconds
import platform.Foundation.NSLog
import platform.Foundation.NSError
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual class StreamingPlayer {
        private var avPlayer: AVPlayer? = null
        private var positionUpdateJob: Job? = null
        private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        private var observer: Any? = null
        private var statusObserverJob: Job? = null
        private var playbackSpeed = 1.0f

        private val _currentPositionMs = MutableStateFlow(0L)
        actual val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

        private val _durationMs = MutableStateFlow(0L)
        actual val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

        private val _isPlaying = MutableStateFlow(false)
        actual val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

        private val _isReady = MutableStateFlow(false)
        actual val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

        @OptIn(BetaInteropApi::class)
        actual suspend fun prepare(url: String) {
                withContext(Dispatchers.Main) {
                        try {
                                cleanupPlayer()

                                memScoped {
                                        val categoryErrorPtr = alloc<ObjCObjectVar<NSError?>>()
                                        val audioSession = AVAudioSession.sharedInstance()
                                        val categorySuccess = audioSession.setCategory(
                                                AVAudioSessionCategoryPlayback,
                                                error = categoryErrorPtr.ptr
                                        )
                                        if (!categorySuccess) {
                                                NSLog("$TAG Failed to set audio category: ${categoryErrorPtr.value?.localizedDescription}")
                                        }

                                        val activeErrorPtr = alloc<ObjCObjectVar<NSError?>>()
                                        val activeSuccess = audioSession.setActive(true, error = activeErrorPtr.ptr)
                                        if (!activeSuccess) {
                                                NSLog("$TAG Failed to activate audio session: ${activeErrorPtr.value?.localizedDescription}")
                                        }
                                }

                                val nsUrl = NSURL.URLWithString(url) ?: run {
                                        _isReady.value = false
                                        return@withContext
                                }

                                val playerItem = AVPlayerItem(uRL = nsUrl)
                                avPlayer = AVPlayer(playerItem = playerItem)

                                // TODO:  прочитать про KVO
                                statusObserverJob = scope.launch {
                                        var attempts = 0
                                        val maxAttempts = 100

                                        while (isActive && !_isReady.value && attempts < maxAttempts) {
                                                delay(100)
                                                attempts++

                                                val item = avPlayer?.currentItem
                                                if (item == null) {
                                                        NSLog("$TAG Player item is null")
                                                        continue
                                                }

                                                when (item.status) {
                                                        AVPlayerItemStatusReadyToPlay -> {
                                                                val durationSeconds = CMTimeGetSeconds(item.duration)

                                                                if (!durationSeconds.isNaN() &&
                                                                        !durationSeconds.isInfinite() &&
                                                                        durationSeconds > 0
                                                                ) {
                                                                        _durationMs.value = (durationSeconds * 1000).toLong()
                                                                        _isReady.value = true
                                                                }
                                                        }

                                                        AVPlayerItemStatusFailed -> {
                                                                NSLog("$TAG Failed to load: ${item.error?.localizedDescription}")
                                                                _isReady.value = false
                                                                break
                                                        }

                                                        else -> Unit
                                                }
                                        }

                                        if (attempts >= maxAttempts && !_isReady.value) {
                                                NSLog("$TAG Timeout waiting for player to be ready")
                                                _isReady.value = false
                                        }
                                }

                                observer = NSNotificationCenter.defaultCenter.addObserverForName(
                                        name = AVPlayerItemDidPlayToEndTimeNotification,
                                        `object` = playerItem,
                                        queue = NSOperationQueue.mainQueue
                                ) { _ ->
                                        _isPlaying.value = false
                                        stopPositionUpdates()
                                }
                                check(waitUntilReady()) { "Player is not ready" }

                        } catch (e: Exception) {
                                NSLog("$TAG Error preparing player: ${e.message}")
                                _isReady.value = false
                        }
                }
        }

        actual fun play() {
                scope.launch {
                        avPlayer?.let { player ->
                                if (!_isReady.value) return@launch
                                player.setRate(playbackSpeed)
                                _isPlaying.value = true
                                startPositionUpdates()
                        }
                }
        }

        actual fun pause() {
                scope.launch {
                        avPlayer?.pause()
                        _isPlaying.value = false
                        stopPositionUpdates()
                }
        }

        actual fun seekTo(positionMs: Long) {
                scope.launch {
                        avPlayer?.let { player ->
                                val durationMs = _durationMs.value
                                val safePosition = if (durationMs > 0) positionMs.coerceIn(0, durationMs)
                                else positionMs.coerceAtLeast(0)
                                val time = CMTimeMake(value = safePosition, timescale = 1000)
                                player.seekToTime(time)
                                _currentPositionMs.value = safePosition
                        }
                }
        }

        actual fun rewind(milliseconds: Long) {
                val newPosition = (_currentPositionMs.value - milliseconds).coerceAtLeast(0)
                seekTo(newPosition)
        }

        actual fun setPlaybackSpeed(speed: Float) {
                playbackSpeed = speed
                scope.launch {
                        avPlayer?.let { player ->
                                if (_isPlaying.value || player.timeControlStatus == AVPlayerTimeControlStatusPlaying) {
                                        player.setRate(speed)
                                        _isPlaying.value = true
                                        startPositionUpdates()
                                }
                        }
                }
        }

        actual fun release() {
                cleanupPlayer()
                scope.cancel()
        }

        private fun cleanupPlayer() {
                statusObserverJob?.cancel()
                statusObserverJob = null

                observer?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
                observer = null

                avPlayer?.pause()
                avPlayer = null

                _isPlaying.value = false
                _isReady.value = false
                _currentPositionMs.value = 0
                _durationMs.value = 0
        }

        private suspend fun waitUntilReady(): Boolean {
                return withTimeoutOrNull(READY_TIMEOUT_MS) {
                        isReady.first { it }
                        true
                } == true
        }

        private fun startPositionUpdates() {
                stopPositionUpdates()
                positionUpdateJob = scope.launch {
                        while (isActive) {
                                avPlayer?.let { player ->
                                        if (player.timeControlStatus == AVPlayerTimeControlStatusPlaying) {
                                                val currentSeconds = CMTimeGetSeconds(player.currentTime())
                                                if (!currentSeconds.isNaN()) {
                                                        _currentPositionMs.value = (currentSeconds * 1000).toLong()
                                                }
                                        }
                                }

                                delay(100)
                        }
                }
        }

        private fun stopPositionUpdates() {
                positionUpdateJob?.cancel()
                positionUpdateJob = null
        }

        private companion object {
                const val TAG = "StreamingPlayer.iOS"
                const val READY_TIMEOUT_MS = 10_000L
        }
}
