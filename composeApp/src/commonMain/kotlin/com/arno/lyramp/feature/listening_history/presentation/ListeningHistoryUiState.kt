package com.arno.lyramp.feature.listening_history.presentation

import com.arno.lyramp.feature.listening_history.model.MusicTrack

sealed interface ListeningHistoryUiState {
        data object Loading : ListeningHistoryUiState
        data object Empty : ListeningHistoryUiState
        data class Success(val tracks: List<MusicTrack>) : ListeningHistoryUiState
        data class Error(val message: String) : ListeningHistoryUiState
}