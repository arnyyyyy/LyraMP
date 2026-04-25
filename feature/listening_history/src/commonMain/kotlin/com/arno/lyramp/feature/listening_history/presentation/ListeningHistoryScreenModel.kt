package com.arno.lyramp.feature.listening_history.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.authorization.domain.CompleteYandexLoginUseCase
import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.domain.model.PlaylistSource
import com.arno.lyramp.feature.listening_history.domain.usecase.AddManualTrackUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetListeningHistoryUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetPlaylistSourcesUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.HideTrackUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.RemovePlaylistSourceUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.SavePlaylistUrlUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.UpdateTrackLanguageUseCase
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

internal class ListeningHistoryScreenModel(
        private val getListeningHistory: GetListeningHistoryUseCase,
        private val hideTrack: HideTrackUseCase,
        private val updateTrackLanguage: UpdateTrackLanguageUseCase,
        private val addManualTrack: AddManualTrackUseCase,
        private val savePlaylistUrl: SavePlaylistUrlUseCase,
        private val getPlaylistSources: GetPlaylistSourcesUseCase,
        private val removePlaylistSource: RemovePlaylistSourceUseCase,
        observeSelectedLanguage: ObserveSelectedLanguageUseCase,
        private val saveSelectedLanguage: SaveSelectedLanguageUseCase,
        private val getLearningLanguages: GetLearningLanguagesUseCase,
        private val getLastAuthorizedService: GetLastAuthorizedServiceUseCase,
        private val completeYandexLogin: CompleteYandexLoginUseCase,
) : ScreenModel {

        private val _isYandexAuthorized = MutableStateFlow(getLastAuthorizedService() == MusicServiceType.YANDEX.name)
        val isYandexAuthorized: StateFlow<Boolean> = _isYandexAuthorized.asStateFlow()

        val isPracticeAvailable: Boolean
                get() = _isYandexAuthorized.value
        private val _uiState =
                MutableStateFlow<ListeningHistoryUiState>(ListeningHistoryUiState.Loading)
        val uiState: StateFlow<ListeningHistoryUiState> = _uiState.asStateFlow()

        private val _allTracks = MutableStateFlow<List<ListeningHistoryMusicTrack>>(emptyList())

        val selectedLanguage: StateFlow<String?> = observeSelectedLanguage()

        private val _availableLanguages = MutableStateFlow<List<String>>(emptyList())
        val availableLanguages: StateFlow<List<String>> = _availableLanguages.asStateFlow()

        private val _isRefreshing = MutableStateFlow(false)
        val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

        private val _playlistSources = MutableStateFlow<List<PlaylistSource>>(emptyList())
        val playlistSources: StateFlow<List<PlaylistSource>> = _playlistSources.asStateFlow()

        init {
                refreshPlaylistSources()
                loadHistory()
                screenModelScope.launch {
                        selectedLanguage.collect { updateFilteredTracks() }
                }
        }

        private fun loadHistory() {
                screenModelScope.launch {
                        getListeningHistory()
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
                                getListeningHistory()
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

        fun selectLanguage(language: String) {
                saveSelectedLanguage(language)
        }

        fun hideTrack(track: ListeningHistoryMusicTrack) {
                val trackId = track.id ?: return
                screenModelScope.launch {
                        hideTrack(trackId)
                        _allTracks.value = _allTracks.value.filter { it.id != trackId }
                        updateFilteredTracks()
                }
        }

        fun updateTrackLanguage(track: ListeningHistoryMusicTrack, language: String) {
                val trackId = track.id ?: return
                screenModelScope.launch {
                        updateTrackLanguage(trackId, language)
                        _allTracks.value = _allTracks.value.map {
                                if (it.id == trackId) it.copy(language = language) else it
                        }
                        updateFilteredTracks()
                }
        }

        fun onPlaylistUrlChanged(url: String) {
                savePlaylistUrl(url)
                refreshPlaylistSources()
                refresh()
        }

        fun removePlaylistSource(sourceId: String) {
                screenModelScope.launch {
                        removePlaylistSource.invoke(sourceId)
                        refreshPlaylistSources()
                        _allTracks.value = _allTracks.value.filter { it.sourceId != sourceId }
                        updateFilteredTracks()
                        refresh()
                }
        }

        fun addManualTrack(name: String, artist: String) {
                screenModelScope.launch {
                        val track = addManualTrack.invoke(name, artist, selectedLanguage.value)
                        _allTracks.value = listOf(track) + _allTracks.value
                        refreshLanguagesInternal()
                }
        }

        private fun refreshPlaylistSources() {
                _playlistSources.value = getPlaylistSources()
        }

        fun onYandexLoginSuccess(token: String, expiresIn: Long?) {
                completeYandexLogin(token, expiresIn)
                _isYandexAuthorized.value = getLastAuthorizedService() == MusicServiceType.YANDEX.name
                refresh()
        }
}
