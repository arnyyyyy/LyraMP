package com.arno.lyramp.util

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

actual class AudioFileManager actual constructor() : KoinComponent {
        private val context: Context by inject()

        actual fun getAudioFilePath(word: String, sourceLang: String): String {
                val sanitized = word.replace(Regex("[^a-zA-Z0-9а-яА-ЯёЁ]"), "_")
                val fileName = "audio_${sanitized}_${sourceLang}.mp3"
                return File(getAudioDirectory(), fileName).absolutePath
        }

        private fun getAudioDirectory(): File {
                val audioDir = File(context.filesDir, "audio")
                if (!audioDir.exists()) audioDir.mkdirs()
                return audioDir
        }

        actual fun saveAudioFile(word: String, sourceLang: String, bytes: ByteArray): String {
                val filePath = getAudioFilePath(word, sourceLang)
                val file = File(filePath)
                file.writeBytes(bytes)
                return filePath
        }

        actual fun audioFileExists(word: String, sourceLang: String): Boolean {
                val filePath = getAudioFilePath(word, sourceLang)
                return File(filePath).exists()
        }
}
