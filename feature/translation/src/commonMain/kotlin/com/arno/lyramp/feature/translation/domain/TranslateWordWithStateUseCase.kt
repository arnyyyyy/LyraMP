package com.arno.lyramp.feature.translation.domain

class TranslateWordWithStateUseCase internal constructor(
        private val translationRepository: TranslationRepository
) {
        suspend operator fun invoke(word: String): TranslationState {
                return translationRepository.translateWord(word)
        }
}
