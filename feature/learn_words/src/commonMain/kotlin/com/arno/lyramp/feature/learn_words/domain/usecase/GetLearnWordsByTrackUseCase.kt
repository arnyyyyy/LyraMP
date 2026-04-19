package com.arno.lyramp.feature.learn_words.domain.usecase

import com.arno.lyramp.feature.learn_words.data.LearnWordDao

class GetLearnWordsByTrackUseCase internal constructor(
        private val dao: LearnWordDao
) {
        suspend operator fun invoke(albumId: String, trackIndex: Int) = dao.getByAlbumIdAndTrackIndex(albumId, trackIndex)
}

