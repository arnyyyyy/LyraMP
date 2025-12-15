package com.arno.lyramp.feature.lyrics.presentation

import com.arno.lyramp.feature.lyrics.model.LyricData

sealed class LyricsState {
        data class Success(val lyrics: List<LyricData>) : LyricsState()
        data class Error(val message: String) : LyricsState()
        object Loading : LyricsState()
}