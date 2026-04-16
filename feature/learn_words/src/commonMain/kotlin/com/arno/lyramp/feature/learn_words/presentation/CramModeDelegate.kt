package com.arno.lyramp.feature.learn_words.presentation

import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class CramModeDelegate(
        private val shuffledWords: List<LearnWordEntity>,
        private val repository: LearnWordsRepository,
        private val uiState: MutableStateFlow<LearnWordsUiState>,
        private val coroutineScope: CoroutineScope,
) {
        private var correctCount = 0
        private var incorrectCount = 0

        init {
                emitState(0)
        }

        fun onInputChange(input: String) {
                val state = uiState.value as? LearnWordsUiState.Cram ?: return
                uiState.value = state.copy(userInput = input, result = null)
        }

        fun onCheck() {
                val state = uiState.value as? LearnWordsUiState.Cram ?: return
                if (state.result != null) return

                val correct = state.word.word.trim().equals(state.userInput.trim(), ignoreCase = true)
                if (correct) correctCount++ else incorrectCount++

                uiState.value = state.copy(
                        result = if (correct) CheckResult.CORRECT else CheckResult.INCORRECT,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount
                )
                if (correct) {
                        coroutineScope.launch { incrementWordProgress(state.word.id) }
                }
        }

        fun onNext() {
                val state = uiState.value as? LearnWordsUiState.Cram ?: return
                val next = state.currentIndex + 1
                if (next >= shuffledWords.size) {
                        uiState.value = LearnWordsUiState.Completed(
                                mode = LearningMode.CRAM,
                                correctCount = correctCount,
                                incorrectCount = incorrectCount,
                                totalCount = shuffledWords.size
                        )
                } else {
                        emitState(next)
                }
        }

        private fun emitState(index: Int) {
                val word = shuffledWords[index]
                uiState.value = LearnWordsUiState.Cram(
                        word = word.toDomain(),
                        userInput = "",
                        result = null,
                        currentIndex = index,
                        totalCount = shuffledWords.size,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount
                )
        }

        private suspend fun incrementWordProgress(wordId: Long) {
                repository.incrementProgress(wordId)
        }
}

