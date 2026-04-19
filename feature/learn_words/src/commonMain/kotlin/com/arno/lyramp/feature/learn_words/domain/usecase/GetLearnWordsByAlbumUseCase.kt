package com.arno.lyramp.feature.learn_words.domain.usecase

import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository

class GetLearnWordsByAlbumUseCase internal constructor(
        private val repository: LearnWordsRepository
) {
        suspend operator fun invoke(albumId: String) = repository.getByAlbumId(albumId)
}

