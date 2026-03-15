package com.arno.lyramp.util

import android.media.MediaPlayer
import android.util.Log

actual class AudioPlayer {
        private var mediaPlayer: MediaPlayer? = null

        actual fun play(filePath: String, onCompletion: () -> Unit) {
                release()
                try {
                        mediaPlayer = MediaPlayer().apply {
                                setDataSource(filePath)
                                setOnCompletionListener { onCompletion() }
                                prepare()
                                start()
                        }
                } catch (e: Exception) {
                        Log.e(TAG, "Play error: ${e.message}", e)
                        release()
                }
        }

        actual fun stop() {
                mediaPlayer?.apply { if (isPlaying) stop() }
        }

        actual fun release() {
                mediaPlayer?.release()
                mediaPlayer = null
        }

        actual fun setPlaybackSpeed(speed: Float) {
                try {
                        mediaPlayer?.let {
                                it.playbackParams = it.playbackParams.setSpeed(speed)
                        }
                } catch (e: Exception) {
                        Log.e(TAG, "setPlaybackSpeed: ${e.message}")
                }
        }

        private companion object {
                const val TAG = "AudioPlayer.Android"
        }
}