package com.arno.lyramp.feature.learn_words.domain.usecase

import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository

class SaveLearnWordUseCase internal constructor(
        private val repository: LearnWordsRepository
) {
        suspend operator fun invoke(
                word: String,
                translation: String,
                sourceLang: String?,
                trackName: String,
                artists: List<String>,
                lyricLine: String,
                albumId: String? = null,
                trackIndex: Int? = null,
                isKnown: Boolean = false
        ) = repository.saveWord(word, translation, sourceLang, trackName, artists, lyricLine, albumId, trackIndex, isKnown)
}
