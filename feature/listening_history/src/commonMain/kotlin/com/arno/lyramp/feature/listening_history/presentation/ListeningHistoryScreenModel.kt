package com.arno.lyramp.feature.listening_history.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.authorization.domain.CompleteYandexLoginUseCase
import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.listening_history.domain.model.PlaylistSource
import com.arno.lyramp.feature.listening_history.domain.usecase.AddManualTrackUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetListeningHistoryUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetLocalListeningHistoryUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetPlaylistSourcesUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.HideTrackUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.PrefetchLyricsForRecentTracksUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.RemovePlaylistSourceUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.ResolveRemainingsByYandexUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.SavePlaylistUrlUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.UpdateTrackLanguageUseCase
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryUiState.Error
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLearningLanguagesUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.ObserveSelectedLanguageUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.SaveSelectedLanguageUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class ListeningHistoryScreenModel(
        private val getListeningHistory: GetListeningHistoryUseCase,
        private val getLocalListeningHistory: GetLocalListeningHistoryUseCase,
        private val hideTrackUseCase: HideTrackUseCase,
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
        private val resolveRemainingsByYandex: ResolveRemainingsByYandexUseCase,
        private val prefetchLyrics: PrefetchLyricsForRecentTracksUseCase,
        private val uiStateBuilder: ListeningHistoryUiStateBuilder = ListeningHistoryUiStateBuilder(),
) : ScreenModel {

        private val _isYandexAuthorized = MutableStateFlow(getLastAuthorizedService() == MusicServiceType.YANDEX.name)
        val isYandexAuthorized: StateFlow<Boolean> = _isYandexAuthorized.asStateFlow()

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

        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

        private val _selectedSourceId = MutableStateFlow<String?>(null)
        val selectedSourceId: StateFlow<String?> = _selectedSourceId.asStateFlow()

        private val _folderItems = MutableStateFlow<List<FolderItem>>(emptyList())
        val folderItems: StateFlow<List<FolderItem>> = _folderItems.asStateFlow()

        private var syncJob: Job? = null
        private var pendingSyncAfterCurrent = false

        init {
                refreshPlaylistSources()
                observeLocalListeningHistory()
                loadHistory()
                screenModelScope.launch {
                        selectedLanguage.collect {
                                updateFilteredTracks(
                                        showEmptyState = _uiState.value !is ListeningHistoryUiState.Loading &&
                                            _uiState.value !is Error
                                )
                        }
                }
        }

        private fun observeLocalListeningHistory() {
                screenModelScope.launch {
                        getLocalListeningHistory.observe().collect { tracks ->
                                applyTracks(
                                        tracks = tracks,
                                        showEmptyState = _uiState.value !is ListeningHistoryUiState.Loading
                                            && _uiState.value !is Error,
                                )
                        }
                }
        }

        private fun loadHistory() = startSync(showRefreshing = false, forceRestart = false)

        fun refreshLanguages() = refreshLanguagesInternal() // TODO

        private fun refreshLanguagesInternal(showEmptyState: Boolean = true) {
                val languages = uiStateBuilder.availableLanguages(
                        tracks = _allTracks.value,
                        learningLanguages = getLearningLanguages(),
                )
                _availableLanguages.value = languages

                val current = selectedLanguage.value
                if (current == null || current !in languages) {
                        saveSelectedLanguage(languages.firstOrNull())
                }

                updateFilteredTracks(showEmptyState)
        }

        private fun updateFilteredTracks(showEmptyState: Boolean = true) {
                val filtered = getFilteredTracks()
                _uiState.value = uiStateBuilder.stateFor(
                        filteredTracks = filtered,
                        currentState = _uiState.value,
                        showEmptyState = showEmptyState,
                )
        }

        private fun getFilteredTracks(): List<ListeningHistoryMusicTrack> = uiStateBuilder.filteredTracks(
                tracks = _allTracks.value,
                selectedLanguage = selectedLanguage.value,
                selectedSourceId = _selectedSourceId.value,
                searchQuery = _searchQuery.value,
        )

        fun setSearchQuery(query: String) {
                _searchQuery.value = query
                updateFilteredTracks()
        }

        fun setSourceFilter(sourceId: String?) {
                _selectedSourceId.value = sourceId
                updateFilteredTracks()
        }

        fun refresh() = startSync(showRefreshing = true, forceRestart = false)

        private fun startSync(showRefreshing: Boolean, forceRestart: Boolean) {
                val activeJob = syncJob
                if (activeJob?.isActive == true) {
                        if (forceRestart) pendingSyncAfterCurrent = true
                        return
                }

                syncJob = screenModelScope.launch {
                        if (showRefreshing) {
                                _isRefreshing.value = true
                        }
                        try {
                                loadHistoryInternal(resolveAfterLoad = true)
                                if (showRefreshing) {
                                        runCatching { prefetchLyrics(maxTracks = 5) }
                                }
                        } catch (ce: CancellationException) {
                                throw ce
                        } finally {
                                if (showRefreshing) {
                                        _isRefreshing.value = false
                                }
                                val shouldRunPendingSync = pendingSyncAfterCurrent
                                pendingSyncAfterCurrent = false
                                syncJob = null
                                if (shouldRunPendingSync) {
                                        startSync(showRefreshing = true, forceRestart = false)
                                }
                        }
                }
        }

        fun selectLanguage(language: String) = saveSelectedLanguage(language)

        fun hideTrack(track: ListeningHistoryMusicTrack) {
                _allTracks.value = _allTracks.value.filter { it != track }
                updateFilteredTracks()
                screenModelScope.launch { hideTrackUseCase(track) }
        }

        fun updateTrackLanguage(track: ListeningHistoryMusicTrack, language: String) {
                val trackId = track.id ?: return
                screenModelScope.launch { updateTrackLanguage(trackId, language) }
        }

        fun onPlaylistUrlChanged(url: String) {
                savePlaylistUrl(url)
                refreshPlaylistSources()
                startSync(showRefreshing = true, forceRestart = true)
        }

        fun removePlaylistSource(sourceId: String) {
                screenModelScope.launch {
                        removePlaylistSource.invoke(sourceId)
                        refreshPlaylistSources()
                        startSync(showRefreshing = true, forceRestart = true)
                }
        }

        fun addManualTrack(name: String, artist: String) = screenModelScope.launch {
                addManualTrack(name, artist, selectedLanguage.value)
        }

        private fun refreshPlaylistSources() {
                _playlistSources.value = getPlaylistSources()
                rebuildFolderItems()
        }

        fun onYandexLoginSuccess(token: String, expiresIn: Long?) {
                completeYandexLogin(token, expiresIn)
                _isYandexAuthorized.value = getLastAuthorizedService() == MusicServiceType.YANDEX.name
                startSync(showRefreshing = true, forceRestart = true)
        }

        private suspend fun loadHistoryInternal(resolveAfterLoad: Boolean) {
                if (_allTracks.value.isEmpty()) _uiState.value = ListeningHistoryUiState.Loading
                val synced = syncListeningHistory() ?: return
                applyTracks(synced)
                if (resolveAfterLoad && _isYandexAuthorized.value) resolveRemainingsByYandex()
        }

        private suspend fun syncListeningHistory(): List<ListeningHistoryMusicTrack>? {
                return runCatching { getListeningHistory() }.getOrElse { e ->
                        if (e is CancellationException) throw e
                        if (_allTracks.value.isEmpty()) {
                                _uiState.value = Error(e.message ?: "Unknown error")
                        }
                        null
                }
        }

        private fun applyTracks(tracks: List<ListeningHistoryMusicTrack>, showEmptyState: Boolean = true) {
                _allTracks.value = tracks
                rebuildFolderItems()
                refreshLanguagesInternal(showEmptyState)
        }

        private fun rebuildFolderItems() {
                _folderItems.value = uiStateBuilder.folderItems(
                        tracks = _allTracks.value,
                        playlistSources = _playlistSources.value,
                )
        }
}
