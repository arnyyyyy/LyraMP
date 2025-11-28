package com.arno.lyramp.feature.lyrics.ui

internal sealed interface LyricsUiState {
        data object Loading : LyricsUiState
        data class Success(val lyrics: String) : LyricsUiState
        data class Error(val message: String) : LyricsUiState
}