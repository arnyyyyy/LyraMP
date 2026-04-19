package com.arno.lyramp.feature.album_suggestion.presentation

internal sealed interface AlbumSelectorUiState {
        data object Loading : AlbumSelectorUiState
        data object Empty : AlbumSelectorUiState
        data class AlbumsList(val albums: List<AlbumSelectorItem>) : AlbumSelectorUiState
        data class Error(val message: String) : AlbumSelectorUiState
}
