package com.arno.lyramp.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSLog
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class)
actual class AudioFileManager actual constructor() {

        private fun getAudioDirectory(): String {
                val paths = NSSearchPathForDirectoriesInDomains(
                        NSDocumentDirectory,
                        NSUserDomainMask,
                        true
                )
                val documentsDirectory = paths.first() as String
                val audioDir = "$documentsDirectory/audio"

                val fileManager = NSFileManager.Companion.defaultManager
                if (!fileManager.fileExistsAtPath(audioDir)) {
                        fileManager.createDirectoryAtPath(
                                audioDir,
                                withIntermediateDirectories = true,
                                attributes = null,
                                error = null
                        )
                }

                return audioDir
        }

        actual fun getAudioFilePath(word: String, sourceLang: String): String {
                val sanitized = word.replace(Regex("[^a-zA-Z0-9а-яА-ЯёЁ]"), "_")
                val fileName = "audio_${sanitized}_${sourceLang}.mp3"
                return "${getAudioDirectory()}/$fileName"
        }

        @OptIn(BetaInteropApi::class)
        actual fun saveAudioFile(word: String, sourceLang: String, bytes: ByteArray): String {
                val filePath = getAudioFilePath(word, sourceLang)

                bytes.usePinned { pinned ->
                        val nsData = NSData.Companion.create(
                                bytes = pinned.addressOf(0),
                                length = bytes.size.toULong()
                        ) as NSData
                        nsData.writeToFile(filePath, atomically = true)
                }

                NSLog("$TAG: $filePath saved successfully")

                return filePath
        }

        actual fun audioFileExists(word: String, sourceLang: String): Boolean {
                val filePath = getAudioFilePath(word, sourceLang)
                return NSFileManager.Companion.defaultManager.fileExistsAtPath(filePath)
        }

        private companion object {
                const val TAG = "AudioFileManager.iOS"
        }
}