package com.arno.lyramp.feature.translation.domain

class TranslateWordUseCase internal constructor(
        private val translationRepository: TranslationRepository
) {
        suspend operator fun invoke(word: String): TranslationState {
                return translationRepository.translateWord(word)
        }
}
