package com.arno.lyramp.feature.lyrics.domain

sealed class LyricsResult {
        data class Found(val lyrics: String, val isTimestamped: Boolean = false) : LyricsResult()

        object NotFound : LyricsResult()
}
