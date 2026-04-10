package com.arno.lyramp.feature.listening_history.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryUiState.Error
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryUiState.Success
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLearningLanguagesUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.ObserveSelectedLanguageUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.SaveSelectedLanguageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ListeningHistoryScreenModel(
        private val repository: ListeningHistoryRepository,
        observeSelectedLanguage: ObserveSelectedLanguageUseCase,
        private val saveSelectedLanguage: SaveSelectedLanguageUseCase,
        private val getLearningLanguages: GetLearningLanguagesUseCase,
) : ScreenModel {
        private val _uiState =
                MutableStateFlow<ListeningHistoryUiState>(ListeningHistoryUiState.Loading)
        val uiState: StateFlow<ListeningHistoryUiState> = _uiState.asStateFlow()

        private val _allTracks = MutableStateFlow<List<ListeningHistoryMusicTrack>>(emptyList())

        val selectedLanguage: StateFlow<String?> = observeSelectedLanguage()

        private val _availableLanguages = MutableStateFlow<List<String>>(emptyList())
        val availableLanguages: StateFlow<List<String>> = _availableLanguages.asStateFlow()

        private val _isRefreshing = MutableStateFlow(false)
        val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

        init {
                loadHistory()
                screenModelScope.launch {
                        selectedLanguage.collect { updateFilteredTracks() }
                }
        }

        private fun loadHistory() {
                screenModelScope.launch {
                        repository.getListeningHistory()
                                .catch { e ->
                                        _uiState.value = Error(e.message ?: "Unknown error")
                                }
                                .collect { tracks ->
                                        _allTracks.value = tracks
                                        refreshLanguagesInternal()
                                }
                }
        }

        fun refreshLanguages() {
                refreshLanguagesInternal()
        }

        private fun refreshLanguagesInternal() {
                val learningLanguages = getLearningLanguages()
                val languages = if (learningLanguages.isNotEmpty()) {
                        learningLanguages.sorted().toList()
                } else {
                        _allTracks.value.mapNotNull { it.language }.distinct().sorted()
                }
                _availableLanguages.value = languages

                val current = selectedLanguage.value
                if (current == null || current !in languages) {
                        saveSelectedLanguage(languages.firstOrNull())
                }

                updateFilteredTracks()
        }

        private fun updateFilteredTracks() {
                val filtered = getFilteredTracks()
                _uiState.value = if (filtered.isEmpty()) {
                        ListeningHistoryUiState.Empty
                } else {
                        Success(filtered)
                }
        }

        private fun getFilteredTracks(): List<ListeningHistoryMusicTrack> {
                val allTracks = _allTracks.value
                val lang = selectedLanguage.value ?: return allTracks
                return allTracks.filter { it.language == lang }
        }

        fun refresh() {
                screenModelScope.launch {
                        _isRefreshing.value = true
                        try {
                                repository.getListeningHistory()
                                        .catch { e ->
                                                _uiState.value = Error(e.message ?: "Unknown error")
                                        }
                                        .collect { tracks ->
                                                _allTracks.value = tracks
                                                refreshLanguagesInternal()
                                        }
                        } finally {
                                _isRefreshing.value = false
                        }
                }
        }

        internal fun selectLanguage(language: String) {
                saveSelectedLanguage(language)
        }
}
