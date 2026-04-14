package com.arno.lyramp.feature.translation.domain

class GetSpeechFilePathUseCase internal constructor(
        private val translationRepository: TranslationRepository
) {
        suspend operator fun invoke(wordInfo: WordInfo): String? {
                return translationRepository.getSourceSpeechFilePath(wordInfo)
        }
}
