package com.arno.lyramp.feature.translation.domain

class TranslateWordUseCase internal constructor(
        private val translationRepository: TranslationRepository
) {
        suspend operator fun invoke(word: String): String {
                return when (val state = translationRepository.translateWord(word)) {
                        is TranslationState.Success -> state.translationWithLang.translation ?: ""
                        else -> ""
                }
        }
}
