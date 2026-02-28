package com.arno.lyramp.feature.translation.speech

import com.arno.lyramp.util.AudioPlayer

class TranslationSpeechController {
        private var currentPlayer: AudioPlayer? = null

        fun stop() {
                currentPlayer?.stop()
                currentPlayer?.release()
                currentPlayer = null
        }

        fun play(filePath: String): AudioPlayer {
                stop()
                return AudioPlayer().also {
                        it.play(filePath)
                        currentPlayer = it
                }
        }
}
