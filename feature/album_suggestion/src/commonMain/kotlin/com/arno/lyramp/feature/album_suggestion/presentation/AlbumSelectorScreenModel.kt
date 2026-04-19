package com.arno.lyramp.feature.album_suggestion.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.album_suggestion.data.AlbumSuggestionRepository
import com.arno.lyramp.feature.listening_history.domain.model.AlbumSuggestionResult
import com.arno.lyramp.feature.listening_history.domain.usecase.GetSuggestedAlbumsUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLearningLanguagesUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class AlbumSelectorScreenModel(
        private val getSuggestedAlbums: GetSuggestedAlbumsUseCase,
        private val repository: AlbumSuggestionRepository,
        private val getLearningLanguages: GetLearningLanguagesUseCase,
        private val getSelectedLanguage: GetSelectedLanguageUseCase
) : ScreenModel {

        private val _uiState = MutableStateFlow<AlbumSelectorUiState>(AlbumSelectorUiState.Loading)
        val uiState: StateFlow<AlbumSelectorUiState> = _uiState.asStateFlow()

        init {
                load()
        }

        fun load() {
                _uiState.value = AlbumSelectorUiState.Loading
                screenModelScope.launch {
                        try {
                                val languages = getLearningLanguages().ifEmpty {
                                        val selected = getSelectedLanguage()
                                        if (selected != null) setOf(selected) else emptySet()
                                }
                                val suggestions = getSuggestedAlbums(languages = languages)
                                if (suggestions.isEmpty()) {
                                        _uiState.value = AlbumSelectorUiState.Empty
                                        return@launch
                                }
                                val progressMap = repository.getAlbumProgressBatch(suggestions.map { it.albumId })
                                val items = suggestions.map { it.toSelectorItem(progressMap) }
                                _uiState.value = AlbumSelectorUiState.AlbumsList(items)
                        } catch (e: Exception) {
                                _uiState.value = AlbumSelectorUiState.Error(
                                        e.message ?: "Loading error"
                                )
                        }
                }
        }

        private fun AlbumSuggestionResult.toSelectorItem(
                progressMap: Map<String, com.arno.lyramp.feature.album_suggestion.data.AlbumProgressInfo>
        ): AlbumSelectorItem {
                val progress = progressMap[albumId]
                return AlbumSelectorItem(
                        albumId = albumId,
                        albumTitle = albumTitle,
                        artistName = artistName,
                        imageUrl = imageUrl,
                        totalTracks = progress?.totalTracks
                )
        }
}
