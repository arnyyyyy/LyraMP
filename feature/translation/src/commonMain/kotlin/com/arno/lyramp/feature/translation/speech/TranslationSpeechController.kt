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
                AudioPlayer().also {
                        it.play(filePath, onCompletion)
                        currentPlayer = it
                }
        }

        fun setPlaybackSpeed(speed: Float) {
                currentPlayer?.setPlaybackSpeed(speed)
        }
}
