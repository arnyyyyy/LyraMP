package com.arno.lyramp.util

import android.media.MediaPlayer
import android.util.Log

actual class AudioPlayer {
        private var mediaPlayer: MediaPlayer? = null

        actual fun play(filePath: String) {
                release()
                try {
                        mediaPlayer = MediaPlayer().apply {
                                setDataSource(filePath)
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

        private companion object {
                const val TAG = "AudioPlayer.Android"
        }
}