package com.arno.lyramp.feature.learn_words.presentation

import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class TestModeDelegate(
        private val shuffledWords: List<LearnWordEntity>,
        private val allFilteredWords: List<LearnWordEntity>,
        private val repository: LearnWordsRepository,
        private val uiState: MutableStateFlow<LearnWordsUiState>,
        private val coroutineScope: CoroutineScope,
) {
        private var correctCount = 0
        private var incorrectCount = 0

        init {
                emitState(0)
        }

        fun onSelectOption(index: Int) {
                val state = uiState.value as? LearnWordsUiState.Test ?: return
                if (state.isAnswered) return

                val correct = state.options[index] == state.correctAnswer
                if (correct) correctCount++ else incorrectCount++

                uiState.value = state.copy(
                        selectedIndex = index,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount
                )
                if (correct) {
                        coroutineScope.launch { incrementWordProgress(state.word.id) }
                }
        }

        fun onNext() {
                val state = uiState.value as? LearnWordsUiState.Test ?: return
                val next = state.currentIndex + 1
                if (next >= shuffledWords.size) {
                        uiState.value = LearnWordsUiState.Completed(
                                mode = LearningMode.TEST,
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
                val variant = TestVariant.entries.random()
                val others = allFilteredWords.filter { it.id != word.id }.shuffled()

                val (correctAnswer, wrongAnswers) = when (variant) {
                        TestVariant.FOREIGN_TO_TRANSLATION ->
                                word.translation to others.take(3).map { it.translation }

                        TestVariant.TRANSLATION_TO_FOREIGN ->
                                word.word to others.take(3).map { it.word }
                }

                uiState.value = LearnWordsUiState.Test(
                        word = word.toDomain(),
                        options = (wrongAnswers + correctAnswer).shuffled(),
                        correctAnswer = correctAnswer,
                        selectedIndex = null,
                        variant = variant,
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

