package com.arno.lyramp.util

import kotlinx.coroutines.flow.StateFlow

expect class StreamingPlayer() {
        val currentPositionMs: StateFlow<Long>
        val durationMs: StateFlow<Long>
        val isPlaying: StateFlow<Boolean>
        val isReady: StateFlow<Boolean>

        suspend fun prepare(url: String)

        fun play()
        fun pause()
        fun seekTo(positionMs: Long)
        fun rewind(milliseconds: Long)
        fun release()
}
