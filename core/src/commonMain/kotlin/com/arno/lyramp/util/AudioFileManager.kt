package com.arno.lyramp.util

expect class AudioFileManager() {
        fun getAudioFilePath(word: String, sourceLang: String): String
        fun saveAudioFile(word: String, sourceLang: String, bytes: ByteArray): String
        fun audioFileExists(word: String, sourceLang: String): Boolean
}