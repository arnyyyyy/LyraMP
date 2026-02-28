package com.arno.lyramp.feature.listening_history.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryUiState.Error
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryUiState.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

internal class ListeningHistoryScreenModel(
        private val repository: ListeningHistoryRepository
) : ScreenModel {
        private val _uiState =
                MutableStateFlow<ListeningHistoryUiState>(ListeningHistoryUiState.Loading)
        val uiState: StateFlow<ListeningHistoryUiState> = _uiState.asStateFlow()

        init {
                screenModelScope.launch {
                        repository.getListeningHistory()
                                .catch { e ->
                                        _uiState.value = Error(e.message ?: "Unknown error")
                                }
                                .collect { tracks ->
                                        _uiState.value = if (tracks.isEmpty()) ListeningHistoryUiState.Empty
                                        else Success(tracks)
                                }
                }
        }
}
