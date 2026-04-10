package com.arno.lyramp.feature.lyrics.ui

sealed interface LyricsUiState {
        object Loading : LyricsUiState
        data class Success(val lyricsLines: List<List<String>>) : LyricsUiState
        data class Error(val message: String) : LyricsUiState
}
