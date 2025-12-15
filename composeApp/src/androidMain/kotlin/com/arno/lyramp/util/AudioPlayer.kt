package com.arno.lyramp.util

import android.media.MediaPlayer
import android.util.Log

actual class AudioPlayer {
        private var mediaPlayer: MediaPlayer? = null

        actual fun play(filePath: String) {
                try {
                        stop()
                        release()

                        mediaPlayer = MediaPlayer().apply {
                                setDataSource(filePath)
                                prepare()
                                start()
                        }

                } catch (e: Exception) {
                        Log.e(TAG, e.message ?: "Play error")
                        e.printStackTrace()
                }
        }

        actual fun stop() {
                try {
                        mediaPlayer?.apply {
                                if (isPlaying) {
                                        stop()
                                }
                        }
                } catch (e: Exception) {
                        Log.e(TAG, e.message ?: "Stop error")
                }
        }

        actual fun release() {
                try {
                        mediaPlayer?.release()
                        mediaPlayer = null
                } catch (e: Exception) {
                        Log.e(TAG, e.message ?: "Release error")
                }
        }

        private companion object {
                const val TAG = "AudioPlayer.Android"
        }
}