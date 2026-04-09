package com.arno.lyramp.feature.extraction.domain.usecase

import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.feature.extraction.data.CefrRepository

internal class GetCefrVocabularyUseCase(
        private val cefrRepository: CefrRepository
) {
        suspend operator fun invoke(language: String): Map<String, CefrLevel> {
                return cefrRepository.getVocabularyMap(language)
        }
}
