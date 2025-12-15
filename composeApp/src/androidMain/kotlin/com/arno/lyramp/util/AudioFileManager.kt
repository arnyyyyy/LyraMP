package com.arno.lyramp.util

import android.util.Log
import com.arno.lyramp.MainActivity
import java.io.File

actual class AudioFileManager actual constructor() {
        private fun getAudioDirectory(): File {
                val context = MainActivity.Companion.instance?.applicationContext
                        ?: throw IllegalStateException("MainActivity context not available")

                val audioDir = File(context.filesDir, "audio")
                if (!audioDir.exists()) {
                        audioDir.mkdirs()
                }
                return audioDir
        }

        actual fun getAudioFilePath(word: String, sourceLang: String): String {
                val sanitized = word.replace(Regex("[^a-zA-Z0-9а-яА-ЯёЁ]"), "_")
                val fileName = "audio_${sanitized}_${sourceLang}.mp3"
                return File(getAudioDirectory(), fileName).absolutePath
        }

        actual fun saveAudioFile(word: String, sourceLang: String, bytes: ByteArray): String {
                val filePath = getAudioFilePath(word, sourceLang)
                val file = File(filePath)

                file.writeBytes(bytes)
                Log.d(TAG, " $filePath saved successfully")

                return filePath
        }

        actual fun audioFileExists(word: String, sourceLang: String): Boolean {
                val filePath = getAudioFilePath(word, sourceLang)
                return File(filePath).exists()
        }

        private companion object {
                const val TAG = "AudioFileManager.Android"
        }
}