package com.arno.lyramp.feature.translation.domain

private val cyrillicRegex = Regex("[\u0400-\u04FF]")

class DetectLanguageUseCase(private val translationRepository: TranslationRepository) {
        suspend operator fun invoke(text: String): String? {
                if (cyrillicRegex.containsMatchIn(text)) return "ru"
                return try {
                        when (val state = translationRepository.translateWord(text)) {
                                is TranslationState.Success -> state.translationWithLang.sourceLanguage
                                else -> null
                        }
                } catch (_: Exception) {
                        null
                }
        }
}
