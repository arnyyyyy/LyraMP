package com.arno.lyramp.feature.translation.speech

import com.arno.lyramp.util.AudioPlayer

class TranslationSpeechController {
        private var currentPlayer: AudioPlayer? = null

        fun stop() {
                currentPlayer?.stop()
                currentPlayer?.release()
                currentPlayer = null
        }

        fun play(filePath: String, onCompletion: () -> Unit = {}) {
                stop()
                val player = AudioPlayer()
                currentPlayer = player

                player.play(filePath) {
                        if (currentPlayer !== player) return@play

                        currentPlayer = null
                        player.release()
                        onCompletion()
                }
        }

        fun setPlaybackSpeed(speed: Float) {
                currentPlayer?.setPlaybackSpeed(speed)
        }
}
