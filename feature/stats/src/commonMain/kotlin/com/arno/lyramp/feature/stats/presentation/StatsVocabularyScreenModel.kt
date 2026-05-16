package com.arno.lyramp.feature.stats.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.core.model.CefrDifficultyGroup
import com.arno.lyramp.feature.extraction.domain.usecase.ClassifyWordsByCefrUseCase
import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllLearnWordsUseCase
import com.arno.lyramp.feature.learn_words.domain.usecase.ToggleLearnWordImportanceUseCase
import com.arno.lyramp.ui.VocabularyWordItem
import com.arno.lyramp.ui.VocabularyWordsFilter
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal enum class StatsVocabularyStatus {
        LEARNING,
        LEARNED;

        companion object {
                fun fromName(name: String): StatsVocabularyStatus =
                        entries.firstOrNull { it.name == name } ?: LEARNING
        }
}

internal data class StatsVocabularyUiState(
        val allWords: List<VocabularyWordItem> = emptyList(),
        val visibleWords: List<VocabularyWordItem> = emptyList(),
        val filter: VocabularyWordsFilter = VocabularyWordsFilter.All,
        val availableCefrGroups: Set<CefrDifficultyGroup> = emptySet(),
        val cefrByWord: Map<String, CefrDifficultyGroup> = emptyMap(),
        val isLoading: Boolean = true,
)

internal class StatsVocabularyScreenModel(
        private val language: String,
        statusName: String,
        private val getAllLearnWords: GetAllLearnWordsUseCase,
        private val toggleImportance: ToggleLearnWordImportanceUseCase,
        private val classifyWordsByCefr: ClassifyWordsByCefrUseCase,
) : ScreenModel {
        val status = StatsVocabularyStatus.fromName(statusName)

        private val _uiState = MutableStateFlow(StatsVocabularyUiState())
        val uiState: StateFlow<StatsVocabularyUiState> = _uiState.asStateFlow()

        init {
                screenModelScope.launch {
                        getAllLearnWords().collect { words ->
                                val filtered = words
                                        .filter { it.sourceLang == language }
                                        .filterByStatus()
                                val items = filtered.map { it.toVocabularyItem() }
                                _uiState.value = _uiState.value.copy(
                                        allWords = items,
                                        visibleWords = applyFilter(items, _uiState.value.filter, _uiState.value.cefrByWord),
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
                        toggleImportance(wordId, isImportant)
                }
        }

        private fun loadCefrIfNeeded(words: List<LearnWordEntity>) {
                if (language != "en" || words.isEmpty()) {
                        _uiState.value = _uiState.value.copy(cefrByWord = emptyMap(), availableCefrGroups = emptySet())
                        return
                }
                screenModelScope.launch {
                        try {
                                val groups = classifyWordsByCefr(words.map { it.word }, "en")
                                val cefrByWord = buildMap {
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
                                Log.logger.e(e) { "CEFR loading in StatsVocabularyScreenModel failed" }
                        }
                }
        }

        private fun List<LearnWordEntity>.filterByStatus(): List<LearnWordEntity> = when (status) {
                StatsVocabularyStatus.LEARNING -> filterNot { it.isLearned() }
                StatsVocabularyStatus.LEARNED -> filter { it.isLearned() }
        }

        private fun applyFilter(
                words: List<VocabularyWordItem>,
                filter: VocabularyWordsFilter,
                cefrByWord: Map<String, CefrDifficultyGroup>,
        ): List<VocabularyWordItem> = when (filter) {
                VocabularyWordsFilter.All -> words
                VocabularyWordsFilter.ImportantOnly -> words.filter { it.isImportant }
                is VocabularyWordsFilter.Cefr -> words.filter { cefrByWord[it.word] == filter.group }
        }

        private fun LearnWordEntity.isLearned() = isKnown || progress >= 1f

        private fun LearnWordEntity.toVocabularyItem() = VocabularyWordItem(
                id = id,
                word = word,
                translation = translation,
                isImportant = isImportant,
        )
}
