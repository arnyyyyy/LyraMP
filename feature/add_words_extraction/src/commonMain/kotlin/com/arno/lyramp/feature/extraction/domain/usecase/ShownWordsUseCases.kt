package com.arno.lyramp.feature.extraction.domain.usecase

import com.arno.lyramp.feature.extraction.data.ShownWordsRepository
import com.arno.lyramp.feature.extraction.domain.model.ExtractedWord

internal class MarkWordsAsShownUseCase(
        private val repository: ShownWordsRepository,
) {
        suspend operator fun invoke(words: List<ExtractedWord>) = repository.markShown(words)
}

class MarkWordStringsAsShownUseCase internal constructor(
        private val repository: ShownWordsRepository,
) {
        suspend operator fun invoke(words: List<String>, language: String) =
                repository.markShownStrings(words, language)
}

class GetShownWordsUseCase internal constructor(
        private val repository: ShownWordsRepository,
) {
        suspend fun forStatsLanguage(language: String) = repository.getForStatsLanguage(language)

        suspend fun forExtraction(trackLanguage: String) = repository.getForExtraction(trackLanguage)
}