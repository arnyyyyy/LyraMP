package com.arno.lyramp.feature.learn_words.domain.usecase

import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository

class ToggleLearnWordImportanceUseCase internal constructor(
        private val repository: LearnWordsRepository,
) {
        suspend operator fun invoke(wordId: Long, isImportant: Boolean) =
                repository.toggleImportance(wordId, isImportant)
}
