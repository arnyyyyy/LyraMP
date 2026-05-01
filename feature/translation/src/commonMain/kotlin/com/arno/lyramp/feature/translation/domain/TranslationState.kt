package com.arno.lyramp.feature.translation.domain

import com.arno.lyramp.feature.translation.api.TranslationResult

sealed class TranslationState {
        data class Success(val translationWithLang: TranslationResult) : TranslationState()
        data class Error(val message: String = DEFAULT_TRANSLATION_ERROR_MESSAGE) : TranslationState()
        object EmptyWord : TranslationState()
}

fun TranslationState.displayText(): String {
        return when (this) {
                is TranslationState.Success -> translationWithLang.translation ?: DEFAULT_TRANSLATION_ERROR_MESSAGE
                is TranslationState.Error -> message.ifBlank { DEFAULT_TRANSLATION_ERROR_MESSAGE }
                TranslationState.EmptyWord -> DEFAULT_TRANSLATION_ERROR_MESSAGE
        }
}

private const val DEFAULT_TRANSLATION_ERROR_MESSAGE = "Не удалось перевести"
