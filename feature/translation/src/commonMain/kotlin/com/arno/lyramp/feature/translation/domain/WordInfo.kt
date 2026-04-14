package com.arno.lyramp.feature.translation.domain

data class WordInfo(
        val word: String,
        val translation: String?,
        val sourceLang: String?,
)