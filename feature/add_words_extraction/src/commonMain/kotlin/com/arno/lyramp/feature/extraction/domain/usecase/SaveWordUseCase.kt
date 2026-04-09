package com.arno.lyramp.feature.extraction.domain.usecase

fun interface SaveWordUseCase {
        suspend operator fun invoke(
                word: String,
                translation: String,
                sourceLang: String?,
                trackName: String,
                artists: List<String>,
                lyricLine: String
        )
}
