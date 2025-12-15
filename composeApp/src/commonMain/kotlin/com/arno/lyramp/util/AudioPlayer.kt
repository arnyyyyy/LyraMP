package com.arno.lyramp.util

expect class AudioPlayer() {
        fun play(filePath: String)
        fun stop()
        fun release()
}