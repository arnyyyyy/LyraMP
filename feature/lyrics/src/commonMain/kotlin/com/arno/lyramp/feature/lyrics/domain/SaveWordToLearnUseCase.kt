package com.arno.lyramp.feature.lyrics.domain

fun interface SaveWordToLearnUseCase { // бридж!!!
        suspend operator fun invoke(
                word: String,
                translation: String,
                sourceLang: String?,
                trackName: String,
                artists: List<String>,
                lyricLine: String
        )
}
