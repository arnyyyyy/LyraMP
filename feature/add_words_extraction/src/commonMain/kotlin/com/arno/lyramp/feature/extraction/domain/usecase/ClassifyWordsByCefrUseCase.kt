package com.arno.lyramp.feature.extraction.domain.usecase

import com.arno.lyramp.core.model.CefrDifficultyGroup
import com.arno.lyramp.feature.extraction.data.CefrRepository

// TODO мейби и не пригодится
class ClassifyWordsByCefrUseCase(
        private val cefrRepository: CefrRepository
) {
        suspend operator fun invoke(
                words: List<String>,
                language: String,
        ): Map<CefrDifficultyGroup, List<String>> {
                return cefrRepository.classifyWords(words, language) { it }
        }
}
