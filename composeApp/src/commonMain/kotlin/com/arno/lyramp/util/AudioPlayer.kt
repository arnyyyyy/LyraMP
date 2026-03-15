package com.arno.lyramp.util

expect class AudioPlayer() {
        fun play(filePath: String, onCompletion: () -> Unit = {})
        fun stop()
        fun release()
        fun setPlaybackSpeed(speed: Float)
}