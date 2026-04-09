package com.arno.lyramp.feature.listening_history.domain

import com.arno.lyramp.core.model.TrackInfo
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryDao

class GetRecentTracksUseCase(
        private val historyDao: ListeningHistoryDao
) {
        suspend operator fun invoke(): List<TrackInfo> {
                return historyDao.getAll().map { entity ->
                        TrackInfo(
                                id = entity.trackId,
                                name = entity.name,
                                artists = entity.artists,
                                language = entity.language
                        )
                }
        }
}
