package com.arno.lyramp.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSLog
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual class AudioFileManager actual constructor() {

        actual fun getAudioFilePath(word: String, sourceLang: String): String {
                val sanitized = word.replace(Regex("[^a-zA-Z0-9а-яА-ЯёЁ]"), "_")
                val fileName = "audio_${sanitized}_${sourceLang}.mp3"
                return "${getAudioDirectory()}/$fileName"
        }

        private fun getAudioDirectory(): String {
                val paths = NSSearchPathForDirectoriesInDomains(
                        NSDocumentDirectory,
                        NSUserDomainMask,
                        true
                )
                val documentsDirectory = paths.first() as String
                val audioDir = "$documentsDirectory/audio"

                val fileManager = NSFileManager.defaultManager
                if (!fileManager.fileExistsAtPath(audioDir)) {
                        memScoped {
                                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                                val success = fileManager.createDirectoryAtPath(
                                        audioDir,
                                        withIntermediateDirectories = true,
                                        attributes = null,
                                        error = errorPtr.ptr
                                )
                                if (!success) NSLog("$TAG : Failed to create audio directory : ${errorPtr.value?.localizedDescription}")
                        }
                }

                return audioDir
        }

        actual fun saveAudioFile(word: String, sourceLang: String, bytes: ByteArray): String {
                val filePath = getAudioFilePath(word, sourceLang)

                bytes.usePinned { pinned ->
                        val nsData = NSData.create(
                                bytes = pinned.addressOf(0),
                                length = bytes.size.toULong()
                        )
                        nsData.writeToFile(filePath, atomically = true)
                }

                return filePath
        }

        actual fun audioFileExists(word: String, sourceLang: String): Boolean {
                val filePath = getAudioFilePath(word, sourceLang)
                return NSFileManager.defaultManager.fileExistsAtPath(filePath)
        }

        private companion object {
                const val TAG = "AudioFileManager.iOS"
        }
}