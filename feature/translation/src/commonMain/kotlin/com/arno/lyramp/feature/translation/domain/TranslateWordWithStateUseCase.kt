package com.arno.lyramp.feature.translation.domain

class TranslateWordWithStateUseCase(
        private val translationRepository: TranslationRepository
) {
        suspend operator fun invoke(word: String): TranslationState {
                return translationRepository.translateWord(word)
        }
}
