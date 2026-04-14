package com.arno.lyramp.feature.translation.domain

import com.arno.lyramp.feature.translation.api.TranslationResult

sealed class TranslationState {
        data class Success(val translationWithLang: TranslationResult) : TranslationState()
        data class Error(val message: String) : TranslationState()
        object EmptyWord : TranslationState()
}