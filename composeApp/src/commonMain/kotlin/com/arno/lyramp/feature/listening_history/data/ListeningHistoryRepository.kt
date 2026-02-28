package com.arno.lyramp.feature.listening_history.data

import com.arno.lyramp.feature.listening_history.domain.MusicService
import com.arno.lyramp.feature.listening_history.model.MusicTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class ListeningHistoryRepository(
        private val musicService: MusicService,
        private val dao: MusicTrackDao
) {
        fun getListeningHistory(limit: Int = 20): Flow<List<MusicTrack>> = flow {
                val cachedTracks = dao.getAll()

                if (cachedTracks.isNotEmpty()) {
                        emit(cachedTracks.map { it.toDomain() })

                        try {
                                val fresh = musicService.getListeningHistory(limit)
                                // TODO: обновление через diff
                                dao.deleteAll()
                                dao.insertAll(fresh.map { it.toEntity() })
                                emit(fresh)
                        } catch (_: Exception) {
                        }
                } else {
                        val tracks = musicService.getListeningHistory(limit)
                        dao.deleteAll()
                        dao.insertAll(tracks.map { it.toEntity() })
                        emit(tracks)
                }
        }
}
