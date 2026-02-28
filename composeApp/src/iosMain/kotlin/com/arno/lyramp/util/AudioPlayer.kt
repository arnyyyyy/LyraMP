package com.arno.lyramp.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSError
import platform.Foundation.NSLog
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual class AudioPlayer {
        private var audioPlayer: AVAudioPlayer? = null

        init {
                memScoped<Unit> {
                        val categoryErrorPtr = alloc<ObjCObjectVar<NSError?>>()
                        val audioSession = AVAudioSession.sharedInstance()
                        val categorySuccess = audioSession.setCategory(
                                AVAudioSessionCategoryPlayback,
                                error = categoryErrorPtr.ptr
                        )
                        if (!categorySuccess) {
                                NSLog("$TAG Failed to set audio category: ${categoryErrorPtr.value?.localizedDescription}")
                                return@memScoped
                        }

                        val activeErrorPtr = alloc<ObjCObjectVar<NSError?>>()
                        val activeSuccess = audioSession.setActive(true, error = activeErrorPtr.ptr)
                        if (!activeSuccess) {
                                NSLog("$TAG Failed to activate audio session: ${activeErrorPtr.value?.localizedDescription}")
                        }
                }
        }

        actual fun play(filePath: String) {
                audioPlayer?.stop()
                audioPlayer = null

                val url = NSURL.fileURLWithPath(filePath)

                memScoped {
                        val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                        val player = AVAudioPlayer(contentsOfURL = url, error = errorPtr.ptr)

                        val error = errorPtr.value
                        if (error != null) {
                                NSLog("$TAG ${error.localizedDescription}")
                                return
                        }

                        audioPlayer = player
                        player.prepareToPlay()
                        player.play()
                }
        }

        actual fun stop() {
                audioPlayer?.stop()
        }

        actual fun release() {
                audioPlayer?.stop()
                audioPlayer = null
        }

        private companion object {
                const val TAG = "AudioPlayer.iOS"
        }
}