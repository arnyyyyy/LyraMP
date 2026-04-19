package com.arno.lyramp.feature.album_suggestion.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.album_suggestion.data.AlbumSuggestionRepository
import com.arno.lyramp.feature.album_suggestion.domain.model.CandidateMapper
import com.arno.lyramp.feature.album_suggestion.domain.model.SuggestedWordComparator
import com.arno.lyramp.feature.album_suggestion.domain.usecase.EnsureAlbumExtractedUseCase
import com.arno.lyramp.feature.album_suggestion.domain.usecase.ExtractAndSaveTrackUseCase
import com.arno.lyramp.feature.album_suggestion.domain.usecase.SaveReviewedWordsUseCase
import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllUserWordStringsUseCase
import com.arno.lyramp.feature.learn_words.domain.usecase.ObserveLearnWordsByAlbumUseCase
import com.arno.lyramp.feature.listening_history.domain.model.AlbumWithTracksResult
import com.arno.lyramp.feature.listening_history.domain.usecase.GetAlbumWithTracksUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLanguageSettingsUseCase
import com.arno.lyramp.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class AlbumSuggestionScreenModel(
        private val albumId: String,
        private val getAlbumWithTracks: GetAlbumWithTracksUseCase,
        private val getLanguageSettings: GetLanguageSettingsUseCase,
        private val ensureAlbumExtracted: EnsureAlbumExtractedUseCase,
        private val extractAndSaveTrack: ExtractAndSaveTrackUseCase,
        private val repository: AlbumSuggestionRepository,
        private val saveReviewedWords: SaveReviewedWordsUseCase,
        private val getAllUserWords: GetAllUserWordStringsUseCase,
        observeLearnWordsByAlbum: ObserveLearnWordsByAlbumUseCase,
        private val candidateMapper: CandidateMapper
) : ScreenModel {

        private var albumResult: AlbumWithTracksResult? = null

        private val _nav = MutableStateFlow<NavTarget>(NavTarget.Loading)

        private val _knownWords = MutableStateFlow<Set<String>>(emptySet())

        private val candidatesFlow = repository.observeCandidatesByAlbum(albumId)
        private val learnWordsFlow = observeLearnWordsByAlbum(albumId)

        @OptIn(ExperimentalCoroutinesApi::class)
        val uiState: StateFlow<AlbumSuggestionUiState> = _nav
                .flatMapLatest { nav -> buildUiStateFlow(nav) }
                .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5_000), AlbumSuggestionUiState.Loading)

        init {
                loadAlbumOverview()
        }

        fun loadAlbumOverview() {
                _nav.value = NavTarget.Loading
                screenModelScope.launch {
                        try {
                                val result = albumResult ?: getAlbumWithTracks(albumId).also { albumResult = it }
                                val settings = getLanguageSettings()

                                ensureAlbumExtracted(albumId, result, settings) { extracted, total ->
                                        _nav.value = NavTarget.ExtractionInProgress(extracted, total)
                                }

                                _nav.value = NavTarget.Overview
                        } catch (e: Exception) {
                                Log.logger.e(e) { "Failed to load album overview" }
                                _nav.value = NavTarget.Failed(e.message ?: "Ошибка загрузки альбома")
                        }
                }
        }

        fun openTrack(trackIndex: Int) {
                _nav.value = NavTarget.LoadingTrack
                screenModelScope.launch {
                        try {
                                val result = albumResult ?: return@launch
                                if (result.tracks.getOrNull(trackIndex) == null) return@launch
                                val candidates = repository.getCandidatesByTrack(albumId, trackIndex)

                                if (candidates.isNotEmpty()) {
                                        _knownWords.value = emptySet()
                                        _nav.value = NavTarget.ReviewWords(trackIndex, isAlbumMode = false)
                                } else {
                                        _nav.value = NavTarget.Practice(trackIndex)
                                }
                        } catch (e: Exception) {
                                Log.logger.e(e) { "Failed to open track $trackIndex" }
                                _nav.value = NavTarget.Failed(e.message ?: "Ошибка загрузки слов")
                        }
                }
        }

        fun openAlbumWords() {
                _knownWords.value = emptySet()
                _nav.value = NavTarget.ReviewWords(trackIndex = -1, isAlbumMode = true)
        }

        fun toggleWordKnown(word: String) {
                _knownWords.value = _knownWords.value.toMutableSet().apply {
                        if (word in this) remove(word) else add(word)
                }
        }

        fun toggleSelectAll() {
                val nav = _nav.value as? NavTarget.ReviewWords ?: return
                val currentKnown = _knownWords.value
                screenModelScope.launch {
                        val candidates = if (nav.isAlbumMode)
                                repository.getCandidatesByAlbum(albumId).distinctBy { it.word }
                        else
                                repository.getCandidatesByTrack(albumId, nav.trackIndex)
                        val allWords = candidates.map { it.word }.toSet()

                        val allSelected = currentKnown.none { it in allWords }
                        _knownWords.value = if (allSelected) allWords else emptySet()
                }
        }

        fun saveWordsAndComplete() {
                val nav = _nav.value as? NavTarget.ReviewWords ?: return
                val settings = getLanguageSettings()
                val result = albumResult

                screenModelScope.launch {
                        try {
                                val candidates = if (nav.isAlbumMode)
                                        repository.getCandidatesByAlbum(albumId).distinctBy { it.word }
                                else
                                        repository.getCandidatesByTrack(albumId, nav.trackIndex)

                                val words = candidateMapper.toSuggestedWords(candidates)
                                val known = _knownWords.value
                                val selectedToLearn = words.map { it.word }.filter { it !in known }.toSet()


                                val totalPromoted = saveReviewedWords(
                                        albumId = albumId,
                                        words = words,
                                        selectedToLearn = selectedToLearn,
                                        lang = settings.lang
                                )

                                if (totalPromoted > 0 && !nav.isAlbumMode) {
                                        _nav.value = NavTarget.Practice(nav.trackIndex)
                                } else {
                                        val trackTitle = result?.tracks?.getOrNull(nav.trackIndex)?.title
                                                ?: if (nav.isAlbumMode) result?.title.orEmpty() else ""
                                        _nav.value = NavTarget.Completed(
                                                trackIndex = nav.trackIndex,
                                                trackTitle = trackTitle,
                                                savedCount = totalPromoted,
                                                hasNextTrack = !nav.isAlbumMode &&
                                                    result != null &&
                                                    nav.trackIndex + 1 < result.tracks.size
                                        )
                                }
                        } catch (e: Exception) {
                                Log.logger.e(e) { "Failed to save words" }
                                _nav.value = NavTarget.Failed(e.message ?: "Ошибка сохранения")
                        }
                }
        }

        fun openNextTrack(currentTrackIndex: Int) = openTrack(currentTrackIndex + 1)

        fun reanalyzeTrack(trackIndex: Int) {
                val result = albumResult ?: return
                val track = result.tracks.getOrNull(trackIndex) ?: return

                _nav.value = NavTarget.LoadingTrack
                screenModelScope.launch {
                        try {
                                val settings = getLanguageSettings()
                                val allUserWords = getAllUserWords(settings.lang)
                                val existingCandidates = repository.getCandidatesByTrack(albumId, trackIndex)
                                        .map { it.word }.toSet()

                                extractAndSaveTrack(
                                        albumId = albumId,
                                        trackId = track.trackId,
                                        trackTitle = track.title,
                                        artists = track.artists,
                                        trackIndex = trackIndex,
                                        settings = settings,
                                        knownWords = allUserWords + existingCandidates
                                )

                                val freshCandidates = repository.getCandidatesByTrack(albumId, trackIndex)
                                if (freshCandidates.isNotEmpty()) {
                                        _knownWords.value = emptySet()
                                        _nav.value = NavTarget.ReviewWords(trackIndex, isAlbumMode = false)
                                } else {
                                        _nav.value = NavTarget.Completed(
                                                trackIndex = trackIndex,
                                                trackTitle = track.title,
                                                savedCount = 0,
                                                hasNextTrack = trackIndex + 1 < result.tracks.size
                                        )
                                }
                        } catch (e: Exception) {
                                Log.logger.e(e) { "Failed to reanalyze track" }
                                _nav.value = NavTarget.Failed(e.message ?: "Ошибка анализа")
                        }
                }
        }

        fun backToOverview() {
                _nav.value = NavTarget.Overview
        }

        private fun buildUiStateFlow(nav: NavTarget) = when (nav) {
                is NavTarget.Loading -> flowOf(AlbumSuggestionUiState.Loading)

                is NavTarget.ExtractionInProgress -> flowOf(AlbumSuggestionUiState.ExtractionProgress(nav.extracted, nav.total))

                is NavTarget.LoadingTrack -> flowOf(AlbumSuggestionUiState.LoadingTrackWords)

                is NavTarget.Failed -> flowOf(AlbumSuggestionUiState.Error(nav.message))

                is NavTarget.Completed ->
                        flowOf(
                                AlbumSuggestionUiState.LevelCompleted(
                                        trackIndex = nav.trackIndex,
                                        trackTitle = nav.trackTitle,
                                        savedCount = nav.savedCount,
                                        hasNextTrack = nav.hasNextTrack
                                )
                        )

                is NavTarget.Overview -> buildOverviewFlow()
                is NavTarget.ReviewWords -> buildReviewWordsFlow(nav.trackIndex, nav.isAlbumMode)
                is NavTarget.Practice -> buildPracticeFlow(nav.trackIndex)
        }

        private fun buildOverviewFlow() =
                combine(candidatesFlow, learnWordsFlow) { candidates, learnWords ->
                        val result = albumResult ?: return@combine AlbumSuggestionUiState.Loading
                        val settings = getLanguageSettings()
                        val candidatesByTrack = candidates.groupBy { it.trackIndex }
                        val learnWordsByTrack = learnWords.groupBy { it.trackIndex }

                        val trackStats = result.tracks.map { track ->
                                val trackCandidates = candidatesByTrack[track.trackIndex].orEmpty()
                                val trackLearnWords = learnWordsByTrack[track.trackIndex].orEmpty()
                                val learned = trackLearnWords.count { it.progress >= 1f }
                                TrackStats(
                                        trackIndex = track.trackIndex,
                                        title = track.title,
                                        totalWords = trackCandidates.size + trackLearnWords.size,
                                        learnedWords = learned,
                                        pendingWords = trackCandidates.size
                                )
                        }
                        AlbumSuggestionUiState.AlbumOverview(
                                albumId = result.albumId,
                                albumTitle = result.title,
                                artistName = result.artistName,
                                coverUri = result.coverUri,
                                tracks = trackStats,
                                totalWords = trackStats.sumOf { it.totalWords },
                                learnedWords = trackStats.sumOf { it.learnedWords },
                                levelLabel = settings.levelLabel
                        )
                }

        private fun buildReviewWordsFlow(trackIndex: Int, isAlbumMode: Boolean) =
                combine(candidatesFlow, _knownWords) { allCandidates, known ->
                        val result = albumResult ?: return@combine AlbumSuggestionUiState.Loading
                        val filtered = if (isAlbumMode)
                                allCandidates.distinctBy { it.word }
                        else
                                allCandidates.filter { it.trackIndex == trackIndex }

                        if (filtered.isEmpty()) {
                                _nav.value = NavTarget.Overview
                                return@combine AlbumSuggestionUiState.Loading
                        }

                        val suggested = candidateMapper.toSuggestedWords(filtered).let {
                                if (isAlbumMode) it.sortedWith(SuggestedWordComparator) else it
                        }
                        val trackTitle = if (isAlbumMode) result.title
                        else result.tracks.getOrNull(trackIndex)?.title ?: ""

                        AlbumSuggestionUiState.TrackWordsList(
                                trackIndex = trackIndex,
                                trackTitle = trackTitle,
                                words = suggested,
                                knownWords = known,
                                isAlbumMode = isAlbumMode
                        )
                }

        private fun buildPracticeFlow(trackIndex: Int) =
                learnWordsFlow.map { allLearnWords ->
                        val result = albumResult ?: return@map AlbumSuggestionUiState.Loading
                        val track = result.tracks.getOrNull(trackIndex)
                                ?: return@map AlbumSuggestionUiState.Loading
                        val trackWords = allLearnWords.filter {
                                it.albumId == albumId && it.trackIndex == trackIndex
                        }
                        val inQueue = trackWords.filter { !it.isKnown && it.progress < 1f }

                        if (inQueue.isEmpty() && trackWords.isNotEmpty()) {
                                return@map AlbumSuggestionUiState.LevelCompleted(
                                        trackIndex = trackIndex,
                                        trackTitle = track.title,
                                        savedCount = trackWords.size,
                                        hasNextTrack = trackIndex + 1 < result.tracks.size
                                )
                        }

                        if (trackWords.isEmpty()) {
                                return@map AlbumSuggestionUiState.LevelCompleted(
                                        trackIndex = trackIndex,
                                        trackTitle = track.title,
                                        savedCount = 0,
                                        hasNextTrack = trackIndex + 1 < result.tracks.size
                                )
                        }

                        AlbumSuggestionUiState.TrackPractice(
                                albumId = albumId,
                                trackIndex = trackIndex,
                                trackTitle = track.title,
                                words = inQueue.map { it.toPracticeWord() },
                                totalInTrack = trackWords.size,
                                learnedInTrack = trackWords.count { it.isKnown || it.progress >= 1f }
                        )
                }

        private fun LearnWordEntity.toPracticeWord(): PracticeWord {
                val src = parseSources().firstOrNull()
                return PracticeWord(
                        id = id,
                        word = word,
                        translation = translation,
                        lyricLine = src?.lyricLine.orEmpty(),
                        progress = progress
                )
        }
}
