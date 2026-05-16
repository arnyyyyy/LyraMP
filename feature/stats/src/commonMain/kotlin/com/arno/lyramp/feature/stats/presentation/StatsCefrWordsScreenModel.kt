package com.arno.lyramp.feature.stats.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.core.model.CefrDifficultyGroup
import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.feature.extraction.domain.usecase.GetShownWordsUseCase
import com.arno.lyramp.feature.extraction.domain.usecase.MarkWordStringsAsShownUseCase
import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllLearnWordsUseCase
import com.arno.lyramp.feature.stats.data.StatsTrackCefrWordRepository
import com.arno.lyramp.ui.WordItem
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal sealed interface StatsCefrWordsUiState {
        data object Loading : StatsCefrWordsUiState

        data class Ready(
                val group: CefrDifficultyGroup,
                val words: List<WordItem>,
                val selectedWords: Set<String>,
                val isSaving: Boolean = false,
        ) : StatsCefrWordsUiState

        data class Error(val message: String) : StatsCefrWordsUiState
}

internal class StatsCefrWordsScreenModel(
        private val language: String,
        groupName: String,
        private val cefrWordRepository: StatsTrackCefrWordRepository,
        private val getAllLearnWords: GetAllLearnWordsUseCase,
        private val getShownWords: GetShownWordsUseCase,
        private val markWordStringsAsShown: MarkWordStringsAsShownUseCase,
) : ScreenModel {
        private val group = runCatching {
                CefrDifficultyGroup.valueOf(groupName)
        }.getOrDefault(CefrDifficultyGroup.BEGINNER)

        private val _uiState = MutableStateFlow<StatsCefrWordsUiState>(StatsCefrWordsUiState.Loading)
        val uiState: StateFlow<StatsCefrWordsUiState> = _uiState.asStateFlow()

        init {
                screenModelScope.launch { load() }
        }

        fun toggleWord(word: String) {
                val current = _uiState.value as? StatsCefrWordsUiState.Ready ?: return
                val nextSelected = if (word in current.selectedWords) {
                        current.selectedWords - word
                } else {
                        current.selectedWords + word
                }
                _uiState.value = current.copy(selectedWords = nextSelected)
        }

        fun toggleSelectAll() {
                val current = _uiState.value as? StatsCefrWordsUiState.Ready ?: return
                val allWords = current.words.map { it.word }.toSet()
                _uiState.value = current.copy(
                        selectedWords = if (current.selectedWords.containsAll(allWords)) emptySet() else allWords
                )
        }

        fun save(onSaved: () -> Unit) {
                val current = _uiState.value as? StatsCefrWordsUiState.Ready ?: return
                screenModelScope.launch {
                        _uiState.value = current.copy(isSaving = true)
                        try {
                                markWordStringsAsShown(current.selectedWords.toList(), language)
                                onSaved()
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (e: Exception) {
                                Log.logger.e(e) { "Stats CEFR words save failed" }
                                _uiState.value = current.copy(isSaving = false)
                        }
                }
        }

        private suspend fun load() {
                try {
                        val learnedWords = getAllLearnWords().first()
                                .filter { it.sourceLang == language && it.isLearned() }
                                .mapTo(mutableSetOf()) { it.word.lowercase() }
                        val shownWords = getShownWords.forStatsLanguage(language)
                        val selectedWords = learnedWords + shownWords

                        val words = cefrWordRepository.getForLanguage(language)
                                .asSequence()
                                .filter { word ->
                                        val level = runCatching { CefrLevel.valueOf(word.cefrLevel) }.getOrNull()
                                        level != null && group.includesLevel(level)
                                }
                                .map { it.word.lowercase() to it.cefrLevel }
                                .distinctBy { it.first }
                                .sortedBy { it.first }
                                .map { (word, level) ->
                                        WordItem(
                                                word = word,
                                                subtitle = "",
                                                levelTag = level,
                                        )
                                }
                                .toList()

                        _uiState.value = StatsCefrWordsUiState.Ready(
                                group = group,
                                words = words,
                                selectedWords = selectedWords.intersect(words.map { it.word }.toSet()),
                        )
                } catch (ce: CancellationException) {
                        throw ce
                } catch (e: Exception) {
                        Log.logger.e(e) { "Stats CEFR words load failed" }
                        _uiState.value = StatsCefrWordsUiState.Error(e.message ?: "Unknown error")
                }
        }

        private fun LearnWordEntity.isLearned() = isKnown || progress >= 1f
}
