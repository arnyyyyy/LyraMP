package com.arno.lyramp.feature.learn_words.presentation

import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import com.arno.lyramp.feature.translation.domain.GetSpeechFilePathUseCase
import com.arno.lyramp.feature.translation.domain.WordInfo as TranslationWordInfo
import com.arno.lyramp.feature.translation.speech.TranslationSpeechController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class CardsModeDelegate(
        private val shuffledWords: List<LearnWordEntity>,
        private val repository: LearnWordsRepository,
        private val getSpeechFilePath: GetSpeechFilePathUseCase,
        private val speechController: TranslationSpeechController,
        private val uiState: MutableStateFlow<LearnWordsUiState>,
        private val coroutineScope: CoroutineScope,
) {
        private data class SwipeRecord(
                val wordId: Long,
                val isKnown: Boolean,
                val previousProgress: Float,
                val previousIsKnown: Boolean,
        )

        private var correctCount = 0
        private var incorrectCount = 0
        private val swipeHistory = mutableListOf<SwipeRecord>()

        init {
                uiState.value = LearnWordsUiState.Cards(
                        words = shuffledWords.map { it.toDomain() },
                        currentIndex = 0,
                        correctCount = 0,
                        incorrectCount = 0,
                        totalCount = shuffledWords.size
                )
        }

        fun onSwipe(wordId: Long, isKnown: Boolean) {
                val state = uiState.value as? LearnWordsUiState.Cards ?: return

                coroutineScope.launch {
                        val entity = repository.getById(wordId)
                        val previousProgress = entity?.progress ?: 0f
                        val previousIsKnown = entity?.isKnown ?: false

                        swipeHistory.add(SwipeRecord(wordId, isKnown, previousProgress, previousIsKnown))
                        if (isKnown) correctCount++ else incorrectCount++

                        val next = state.currentIndex + 1
                        if (next >= state.words.size) {
                                uiState.value = LearnWordsUiState.Completed(
                                        mode = LearningMode.CARDS,
                                        correctCount = correctCount,
                                        incorrectCount = incorrectCount,
                                        totalCount = state.totalCount,
                                )
                        } else {
                                uiState.value = state.copy(
                                        currentIndex = next,
                                        correctCount = correctCount,
                                        incorrectCount = incorrectCount,
                                        canUndo = true,
                                        undoDirection = 0,
                                )
                        }

                        repository.markAsKnown(wordId, isKnown)
                        if (isKnown) {
                                repository.incrementProgress(wordId)
                        }
                }
        }

        fun undoLastSwipe() {
                val state = uiState.value
                if (swipeHistory.isEmpty()) return

                val record = swipeHistory.removeLast()
                if (record.isKnown) correctCount-- else incorrectCount--
                correctCount = correctCount.coerceAtLeast(0)
                incorrectCount = incorrectCount.coerceAtLeast(0)

                val direction = if (record.isKnown) -1 else 1

                val previousIndex = when (state) {
                        is LearnWordsUiState.Cards -> state.currentIndex - 1
                        is LearnWordsUiState.Completed -> shuffledWords.size - 1
                        else -> return
                }

                val words = when (state) {
                        is LearnWordsUiState.Cards -> state.words
                        is LearnWordsUiState.Completed -> shuffledWords.map { it.toDomain() }
                        else -> return
                }

                uiState.value = LearnWordsUiState.Cards(
                        words = words,
                        currentIndex = previousIndex,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount,
                        totalCount = shuffledWords.size,
                        canUndo = swipeHistory.isNotEmpty(),
                        undoDirection = direction,
                )

                coroutineScope.launch {
                        repository.updateProgress(record.wordId, record.previousProgress)
                        repository.markAsKnown(record.wordId, record.previousIsKnown)
                }
        }

        fun onToggleImportance(wordId: Long, isImportant: Boolean) {
                coroutineScope.launch {
                        repository.toggleImportance(wordId, !isImportant)
                }
                val state = uiState.value
                if (state is LearnWordsUiState.Cards) {
                        uiState.value = state.copy(
                                words = state.words.map {
                                        if (it.id == wordId) it.copy(isImportant = !isImportant) else it
                                }
                        )
                }
        }

        fun playAudio(word: WordInfo) {
                val state = uiState.value
                if (state is LearnWordsUiState.Cards && state.isLoadingAudio) return

                if (state is LearnWordsUiState.Cards) {
                        uiState.value = state.copy(isLoadingAudio = true)
                }

                coroutineScope.launch {
                        try {
                                val wordInfo = TranslationWordInfo(
                                        word = word.word,
                                        translation = word.translation,
                                        sourceLang = word.sourceLang
                                )
                                val filePath = getSpeechFilePath(wordInfo)
                                if (filePath != null) {
                                        speechController.play(filePath)
                                }
                        } finally {
                                val current = uiState.value
                                if (current is LearnWordsUiState.Cards) {
                                        uiState.value = current.copy(isLoadingAudio = false)
                                }
                        }
                }
        }
}

