package com.arno.lyramp.feature.listening_history.data

import com.arno.lyramp.core.model.TrackInfo
import com.arno.lyramp.feature.listening_history.domain.service.MusicService
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.feature.translation.domain.DetectLanguageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class ListeningHistoryRepository(
        private val musicService: MusicService,
        private val dao: ListeningHistoryDao,
        private val detectLanguage: DetectLanguageUseCase,
) {
        fun getListeningHistory(limit: Int = 50): Flow<List<ListeningHistoryMusicTrack>> = flow {
                val cachedTracks = dao.getAll()

                if (cachedTracks.isNotEmpty()) {
                        emit(cachedTracks.map { it.toDomain() }.filterNonNative())
                        val fresh = musicService.getListeningHistory(limit)
                        applyDiff(cachedTracks, fresh)
                        emit(dao.getAll().map { it.toDomain() }.filterNonNative())
                } else {
                        val tracks = musicService.getListeningHistory(limit)
                        dao.insertAll(tracks.reversed().map { it.withDetectedLanguage().toEntity() })
                        emit(dao.getAll().map { it.toDomain() }.filterNonNative())
                }
        }

        suspend fun getRecentTracks(): List<TrackInfo> {
                return dao.getAll().map { entity ->
                        TrackInfo(
                                id = entity.trackId,
                                name = entity.name,
                                artists = entity.artists,
                                language = entity.language
                        )
                }
        }

        suspend fun saveTrackLanguages(trackLanguages: Map<String, String>) {
                trackLanguages.forEach { (trackId, language) ->
                        dao.updateLanguage(trackId, language)
                }
        }

        suspend fun hideTrack(trackId: String) {
                dao.hideTrack(trackId)
        }

        suspend fun updateTrackLanguage(trackId: String, language: String) {
                dao.updateLanguage(trackId, language)
        }

        suspend fun addManualTrack(name: String, artist: String): ListeningHistoryMusicTrack {
                val id = "${name}||${artist}"
                val language = detectLanguage(name)
                val entity = ListeningHistoryTrackEntity(
                        trackId = id,
                        albumId = null,
                        language = language,
                        name = name,
                        artists = artist,
                        albumName = null,
                        imageUrl = null,
                )
                dao.insertAll(listOf(entity))
                return ListeningHistoryMusicTrack(
                        id = id,
                        name = name,
                        artists = listOf(artist),
                        language = language,
                )
        }

        private suspend fun applyDiff(cached: List<ListeningHistoryTrackEntity>, fresh: List<ListeningHistoryMusicTrack>) {
                val freshIds = fresh.map { it.id ?: "${it.name}||${it.artists.joinToString(",")}" }.toSet()
                val cachedIds = cached.map { it.trackId ?: "${it.name}||${it.artists}" }.toSet()

                val toDelete = cachedIds - freshIds
                if (toDelete.isNotEmpty()) {
                        val remaining = freshIds.toList()
                        if (remaining.isNotEmpty()) dao.deleteNotIn(remaining)
                        else dao.deleteAll()
                }

                val toInsert = fresh.filter { (((it.id ?: "${it.name}||${it.artists.joinToString(",")}")) !in cachedIds) }
                if (toInsert.isNotEmpty())
                        dao.insertAll(toInsert.reversed().map { it.withDetectedLanguage().toEntity() })
        }

        private suspend fun ListeningHistoryMusicTrack.withDetectedLanguage(): ListeningHistoryMusicTrack {
                if (language != null) return this
                val detected = detectLanguage(name) ?: return this
                return copy(language = detected)
        }

        private fun List<ListeningHistoryMusicTrack>.filterNonNative(): List<ListeningHistoryMusicTrack> =
                filter { it.language != "ru" }
}
