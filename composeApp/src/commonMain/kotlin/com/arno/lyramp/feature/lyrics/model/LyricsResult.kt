package com.arno.lyramp.feature.lyrics.model

sealed class LyricsResult {
        data class Success(val lyrics: List<LyricData>) : LyricsResult()
        data class Error(val message: String) : LyricsResult()
        object Loading : LyricsResult()
}