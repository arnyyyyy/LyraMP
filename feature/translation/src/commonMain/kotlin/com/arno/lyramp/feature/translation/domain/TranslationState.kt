package com.arno.lyramp.feature.translation.domain

import com.arno.lyramp.feature.translation.model.TranslationResult

sealed class TranslationState {
        data class Success(val translationWithLang: TranslationResult) : TranslationState()
        data class Error(val message: String) : TranslationState()
        object Loading : TranslationState()
        object EmptyWord : TranslationState()
}