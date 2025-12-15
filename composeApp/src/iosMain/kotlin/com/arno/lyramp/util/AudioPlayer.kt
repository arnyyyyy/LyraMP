package com.arno.lyramp.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSError
import platform.Foundation.NSLog
import platform.Foundation.NSURL
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class)
actual class AudioPlayer {
        private var audioPlayer: AVAudioPlayer? = null

        @OptIn(BetaInteropApi::class)
        actual fun play(filePath: String) {
                dispatch_async(dispatch_get_main_queue()) {
                        try {
                                stop()
                                release()

                                val audioSession = AVAudioSession.Companion.sharedInstance()
                                audioSession.setCategory(AVAudioSessionCategoryPlayback, error = null)
                                audioSession.setActive(true, error = null)

                                val url = NSURL.Companion.fileURLWithPath(filePath)

                                val errorPtr = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
                                val player = AVAudioPlayer(contentsOfURL = url, error = errorPtr.ptr)

                                val error = errorPtr.value
                                if (error != null) {
                                        NSLog("$TAG  ${error.localizedDescription}")
                                        return@dispatch_async
                                }

                                audioPlayer = player
                                player.prepareToPlay()
                                player.play()

                        } catch (e: Exception) {
                                NSLog("$TAG Ошибка воспроизведения: ${e.message}")
                        }
                }
        }

        actual fun stop() {
                audioPlayer?.stop()
        }

        actual fun release() {
                audioPlayer = null
        }

        private companion object {
                const val TAG = "AudioPlayer.iOS"
        }
}