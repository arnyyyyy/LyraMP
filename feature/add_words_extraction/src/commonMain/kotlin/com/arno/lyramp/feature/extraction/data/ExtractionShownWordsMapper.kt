package com.arno.lyramp.feature.extraction.data

import com.arno.lyramp.feature.extraction.domain.model.ExtractedWord

internal class ExtractionShownWordsMapper {
        fun listToEntities(words: List<ExtractedWord>): List<ExtractionShownWordsEntity> {
                return words.map { ExtractionShownWordsEntity(word = it.word) }
        }
}
