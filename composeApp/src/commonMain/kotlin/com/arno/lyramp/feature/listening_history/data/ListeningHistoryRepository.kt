package com.arno.lyramp.feature.listening_history.data

import com.arno.lyramp.feature.listening_history.domain.MusicService
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private val cyrillicRegex = Regex("[\\u0400-\\u04FF]")

internal class ListeningHistoryRepository(
        private val musicService: MusicService,
        private val dao: ListeningHistoryDao
) {
        fun getListeningHistory(limit: Int = 20): Flow<List<ListeningHistoryMusicTrack>> = flow {
                val cachedTracks = dao.getAll()

                if (cachedTracks.isNotEmpty()) {
                        emit(cachedTracks.map { it.toDomain() }.filterNonNative())

                        try {
                                val fresh = musicService.getListeningHistory(limit)
                                applyDiff(cachedTracks, fresh)
                                emit(dao.getAll().map { it.toDomain() }.filterNonNative())
                        } catch (_: Exception) {
                        }
                } else {
                        val tracks = musicService.getListeningHistory(limit)
                        dao.insertAll(tracks.reversed().map { it.preselectLanguage().toEntity() })
                        emit(dao.getAll().map { it.toDomain() }.filterNonNative())
                }
        }

        suspend fun saveTrackLanguages(trackLanguages: Map<String, String>) {
                trackLanguages.forEach { (trackId, language) ->
                        dao.updateLanguage(trackId, language)
                }
        }

        private suspend fun applyDiff(cached: List<ListeningHistoryTrackEntity>, fresh: List<ListeningHistoryMusicTrack>) {
                val freshIds = fresh.mapNotNull { it.id }.toSet()
                val cachedIds = cached.mapNotNull { it.trackId }.toSet()

                val toDelete = cachedIds - freshIds
                if (toDelete.isNotEmpty()) {
                        val remaining = freshIds.toList()
                        if (remaining.isNotEmpty()) dao.deleteNotIn(remaining)
                        else dao.deleteAll()
                }

                val toInsert = fresh.filter { it.id !in cachedIds }
                if (toInsert.isNotEmpty())
                        dao.insertAll(toInsert.reversed().map { it.preselectLanguage().toEntity() })
        }

        private fun ListeningHistoryMusicTrack.preselectLanguage(): ListeningHistoryMusicTrack =
                if (language == null && cyrillicRegex.containsMatchIn(name)) copy(language = "ru")
                else this

        private fun List<ListeningHistoryMusicTrack>.filterNonNative(): List<ListeningHistoryMusicTrack> =
                filter { it.language != "ru" }
}
