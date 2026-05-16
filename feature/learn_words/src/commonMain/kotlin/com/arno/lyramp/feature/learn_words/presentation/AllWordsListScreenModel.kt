package com.arno.lyramp.feature.learn_words.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.core.model.CefrDifficultyGroup
import com.arno.lyramp.ui.VocabularyWordsFilter
import com.arno.lyramp.feature.extraction.domain.usecase.ClassifyWordsByCefrUseCase
import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import com.arno.lyramp.feature.translation.domain.GetSpeechFilePathUseCase
import com.arno.lyramp.feature.translation.domain.WordInfo as TranslationWordInfo
import com.arno.lyramp.feature.translation.speech.TranslationSpeechController
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal data class AllWordsUiState(
        val allWords: List<WordInfo> = emptyList(),
        val visibleWords: List<WordInfo> = emptyList(),
        val filter: VocabularyWordsFilter = VocabularyWordsFilter.All,
        val availableCefrGroups: Set<CefrDifficultyGroup> = emptySet(),
        val cefrByWord: Map<String, CefrDifficultyGroup> = emptyMap(),
        val isLoading: Boolean = true,
        val loadingAudioWordId: Long? = null,
        val playingAudioWordId: Long? = null,
)

internal class AllWordsListScreenModel(
        private val language: String?,
        private val repository: LearnWordsRepository,
        private val classifyWordsByCefr: ClassifyWordsByCefrUseCase,
        private val getSpeechFilePath: GetSpeechFilePathUseCase,
) : ScreenModel {

        private val speechController = TranslationSpeechController()

        private val _uiState = MutableStateFlow(AllWordsUiState())
        val uiState: StateFlow<AllWordsUiState> = _uiState.asStateFlow()

        init {
                screenModelScope.launch {
                        repository.getAllWords().collect { words ->
                                val filtered = if (language != null) {
                                        words.filter { it.sourceLang == language }
                                } else {
                                        words
                                }
                                val domainWords = filtered.map { it.toDomain() }
                                _uiState.value = _uiState.value.copy(
                                        allWords = domainWords,
                                        visibleWords = applyFilter(domainWords, _uiState.value.filter, _uiState.value.cefrByWord),
                                        isLoading = false,
                                )
                                loadCefrIfNeeded(filtered)
                        }
                }
        }

        fun selectFilter(filter: VocabularyWordsFilter) {
                val current = _uiState.value
                _uiState.value = current.copy(
                        filter = filter,
                        visibleWords = applyFilter(current.allWords, filter, current.cefrByWord),
                )
        }

        fun toggleImportant(wordId: Long, isImportant: Boolean) {
                screenModelScope.launch {
                        repository.toggleImportance(wordId, isImportant)
                }
        }

        fun speakWord(word: WordInfo) {
                val current = _uiState.value
                if (current.playingAudioWordId == word.id) {
                        speechController.stop()
                        _uiState.value = current.copy(playingAudioWordId = null, loadingAudioWordId = null)
                        return
                }
                speechController.stop()
                _uiState.value = current.copy(loadingAudioWordId = word.id, playingAudioWordId = null)
                screenModelScope.launch {
                        try {
                                val filePath = getSpeechFilePath(
                                        TranslationWordInfo(
                                                word = word.word,
                                                translation = word.translation,
                                                sourceLang = word.sourceLang,
                                        )
                                )
                                if (filePath != null) {
                                        speechController.play(filePath) {
                                                val latest = _uiState.value
                                                if (latest.playingAudioWordId == word.id) {
                                                        _uiState.value = latest.copy(playingAudioWordId = null)
                                                }
                                        }
                                        _uiState.value = _uiState.value.copy(
                                                loadingAudioWordId = null,
                                                playingAudioWordId = word.id,
                                        )
                                } else {
                                        _uiState.value = _uiState.value.copy(loadingAudioWordId = null)
                                }
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (e: Exception) {
                                Log.logger.e(e) { "speakWord failed for ${word.word}" }
                                _uiState.value = _uiState.value.copy(loadingAudioWordId = null, playingAudioWordId = null)
                        }
                }
        }

        override fun onDispose() {
                speechController.stop()
                super.onDispose()
        }

        private fun loadCefrIfNeeded(words: List<LearnWordEntity>) {
                if (language != "en" || words.isEmpty()) return
                screenModelScope.launch {
                        try {
                                val groups = classifyWordsByCefr(words.map { it.word }, "en")
                                val cefrByWord = buildMap<String, CefrDifficultyGroup> {
                                        groups.forEach { (group, groupWords) ->
                                                groupWords.forEach { put(it, group) }
                                        }
                                }
                                val current = _uiState.value
                                _uiState.value = current.copy(
                                        cefrByWord = cefrByWord,
                                        availableCefrGroups = groups.filterValues { it.isNotEmpty() }.keys,
                                        visibleWords = applyFilter(current.allWords, current.filter, cefrByWord),
                                )
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (e: Exception) {
                                Log.logger.e(e) { "CEFR loading in AllWordsListScreenModel failed" }
                        }
                }
        }

        private fun applyFilter(
                words: List<WordInfo>,
                filter: VocabularyWordsFilter,
                cefrByWord: Map<String, CefrDifficultyGroup>,
        ): List<WordInfo> = when (filter) {
                VocabularyWordsFilter.All -> words
                VocabularyWordsFilter.ImportantOnly -> words.filter { it.isImportant }
                is VocabularyWordsFilter.Cefr -> words.filter { cefrByWord[it.word] == filter.group }
        }
}
