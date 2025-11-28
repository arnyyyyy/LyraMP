package com.arno.lyramp.feature.listening_history.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import com.arno.lyramp.feature.listening_history.domain.MusicService
import com.arno.lyramp.feature.listening_history.ui.ListeningHistoryUiState
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class ListeningHistoryScreenModel(
        private val musicService: MusicService
) : ScreenModel {
        private val scope: CoroutineScope = MainScope()

        private val _uiState =
                MutableStateFlow<ListeningHistoryUiState>(ListeningHistoryUiState.Loading)
        val uiState: StateFlow<ListeningHistoryUiState> = _uiState.asStateFlow()

        init {
                loadTracks()
        }

        fun loadTracks(limit: Int = 20) {
                scope.launch {
                        _uiState.value = ListeningHistoryUiState.Loading
                        try {
                                val tracks = musicService.getListeningHistory(limit)
                                _uiState.value = if (tracks.isEmpty()) {
                                        ListeningHistoryUiState.Empty
                                } else {
                                        ListeningHistoryUiState.Success(tracks)
                                }
                        } catch (e: Throwable) {
                                _uiState.value = ListeningHistoryUiState.Error(e.message ?: "Unknown error")
                        }
                }
        }

        override fun onDispose() {
                scope.cancel()
        }
}

