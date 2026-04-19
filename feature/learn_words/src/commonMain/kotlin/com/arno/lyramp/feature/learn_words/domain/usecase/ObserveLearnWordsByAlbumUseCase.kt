package com.arno.lyramp.feature.learn_words.domain.usecase

import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository

class ObserveLearnWordsByAlbumUseCase internal constructor(
        private val repository: LearnWordsRepository
) {
        operator fun invoke(albumId: String) = repository.observeByAlbumId(albumId)
}
