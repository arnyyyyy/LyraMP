package com.arno.lyramp.feature.learn_words.presentation

import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.data.WordSource

internal data class WordInfo(
        val id: Long,
        val word: String,
        val translation: String,
        val sourceLang: String?,
        val isKnown: Boolean,
        val isImportant: Boolean,
        val sources: List<WordSource>
)

internal fun LearnWordEntity.toDomain(): WordInfo = WordInfo(
        id = id,
        word = word,
        translation = translation,
        sourceLang = sourceLang,
        isKnown = isKnown,
        isImportant = isImportant,
        sources = parseSources()
)

internal enum class LearningMode { CARDS, CRAM, TEST }

internal enum class TestVariant { FOREIGN_TO_TRANSLATION, TRANSLATION_TO_FOREIGN }

internal enum class CheckResult { CORRECT, INCORRECT }