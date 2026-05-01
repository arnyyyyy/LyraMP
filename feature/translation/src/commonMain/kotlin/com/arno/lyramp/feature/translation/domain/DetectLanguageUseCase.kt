package com.arno.lyramp.feature.translation.domain

import kotlinx.coroutines.CancellationException

class DetectLanguageUseCase internal constructor(private val translationRepository: TranslationRepository) {
        suspend operator fun invoke(text: String): String? {
                if (isNativeText(text)) return "ru"
                return try {
                        when (val state = translationRepository.translateWord(text)) {
                                is TranslationState.Success -> state.translationWithLang.sourceLanguage
                                else -> null
                        }
                } catch (e: CancellationException) {
                        throw e
                } catch (_: Exception) {
                        null
                }
        }
}
