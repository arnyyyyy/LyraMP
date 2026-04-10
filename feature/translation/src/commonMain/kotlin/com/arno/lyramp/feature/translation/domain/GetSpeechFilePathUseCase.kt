package com.arno.lyramp.feature.translation.domain

import com.arno.lyramp.feature.translation.model.WordInfo

class GetSpeechFilePathUseCase(
        private val translationRepository: TranslationRepository
) {
        suspend operator fun invoke(wordInfo: WordInfo): String? {
                return translationRepository.getSourceSpeechFilePath(wordInfo)
        }
}
