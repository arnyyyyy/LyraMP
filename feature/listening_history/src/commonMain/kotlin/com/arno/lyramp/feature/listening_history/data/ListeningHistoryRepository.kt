package com.arno.lyramp.feature.listening_history.data

import com.arno.lyramp.core.model.TrackInfo
import com.arno.lyramp.feature.listening_history.domain.service.MusicService
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.feature.listening_history.model.stableKey
import com.arno.lyramp.feature.translation.domain.DetectLanguageUseCase
import kotlinx.coroutines.flow.flow

internal class ListeningHistoryRepository(
        private val musicService: MusicService,
        private val dao: ListeningHistoryDao,
        private val detectLanguage: DetectLanguageUseCase,
        private val syncer: ListeningHistorySyncer = ListeningHistorySyncer(dao),
) {
        fun getListeningHistory(languageDetectionLimit: Int) = flow {
                val cachedTracks = dao.getAll()

                if (cachedTracks.isNotEmpty()) {
                        val fresh = musicService.getListeningHistory(limit = null)
                        syncer.applyDiff(cachedTracks, fresh)
                        detectLanguagesForNextBatch(languageDetectionLimit)
                        emit(dao.getAll().map { it.toDomain() }.filterNonNative())
                } else {
                        val tracks = musicService.getListeningHistory(limit = null)
                        dao.insertAll(tracks.reversed().map { it.toEntity() })
                        detectLanguagesForNextBatch(languageDetectionLimit)
                        emit(dao.getAll().map { it.toDomain() }.filterNonNative())
                }
        }

        suspend fun getRecentTracks() = dao.getAll().map { entity ->
                TrackInfo(
                        id = entity.trackId,
                        name = entity.name,
                        artists = entity.artists,
                        language = entity.language
                )
        }

        suspend fun getAllTracks() = dao.getAll().map { it.toDomain() }

        suspend fun getVisibleTracks() = dao.getAll().map { it.toDomain() }.filterNonNative()

        suspend fun getTracksForLyricsPrefetch(limit: Int) = dao.getTracksForLyricsPrefetch(limit).map { entity ->
                LyricsPrefetchCandidate(
                        localId = entity.localId,
                        name = entity.name,
                        artist = entity.artists.split(",").firstOrNull()?.trim().orEmpty(),
                        trackId = entity.trackId,
                )
        }

        suspend fun setLyricsPrefetchStatus(localId: Long, hasSyncedLyrics: Boolean) {
                dao.setLyricsPrefetchStatus(
                        localId = localId,
                        status = if (hasSyncedLyrics) LYRICS_PREFETCH_STATUS_SYNCED else LYRICS_PREFETCH_STATUS_NOT_SYNCED,
                )
        }

        suspend fun getTracksMissingYandexIds() = dao.getTracksMissingYandexIds().map { entity ->
                TrackResolutionCandidate(
                        localId = entity.localId,
                        name = entity.name,
                        artists = entity.artists.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                )
        }

        suspend fun markYandexResolveAttempted(localId: Long) = dao.markYandexResolveAttempted(localId)

        suspend fun resolveTrackByLocalId(
                localId: Long,
                newTrackId: String,
                albumId: String?,
                albumName: String?,
                artists: List<String>,
        ) {
                if (dao.hasShowingTrackIdExceptLocalId(newTrackId, localId)) {
                        dao.deleteByLocalId(localId)
                } else {
                        dao.resolveTrackByLocalId(
                                localId = localId,
                                newTrackId = newTrackId,
                                albumId = albumId,
                                albumName = albumName,
                                artists = artists.joinToString(","),
                        )
                }
        }

        suspend fun saveTrackLanguages(trackLanguages: Map<String, String>) {
                trackLanguages.forEach { (trackId, language) ->
                        dao.updateLanguage(trackId, language)
                }
        }

        suspend fun prefillFromOnboarding(
                tracks: List<ListeningHistoryMusicTrack>,
                trackLanguages: Map<String, String>
        ) {
                if (dao.count() > 0) return
                val entities = tracks.takeLast(MAX_ONBOARDING_SIZE).reversed().map { track ->
                        val explicit = track.id?.let { trackLanguages[it] } ?: track.language
                        val resolved = explicit ?: detectLanguage(track.name)
                        track.copy(language = resolved).toEntity()
                }
                if (entities.isNotEmpty()) dao.insertAll(entities)
        }

        suspend fun hideTrack(track: ListeningHistoryMusicTrack) = dao.hideTrackByKey(track.stableKey())


        suspend fun updateTrackLanguage(trackId: String, language: String) = dao.updateLanguage(trackId, language)


        suspend fun addManualTrack(name: String, artist: String, language: String? = null): ListeningHistoryMusicTrack {
                val id = "${name}||${artist}"
                val language = language ?: detectLanguage(name)
                val entity = ListeningHistoryTrackEntity(
                        trackId = id,
                        albumId = null,
                        language = language,
                        name = name,
                        artists = artist,
                        albumName = null,
                        imageUrl = null,
                        sourceId = null,
                )
                dao.insertAll(listOf(entity))
                return ListeningHistoryMusicTrack(
                        id = id,
                        name = name,
                        artists = listOf(artist),
                        language = language,
                )
        }

        suspend fun deleteTracksBySourceId(sourceId: String) = dao.deleteBySourceId(sourceId)

        private suspend fun detectLanguagesForNextBatch(limit: Int) {
                if (limit <= 0) return
                dao.getTracksWithoutLanguage().balancedBySource(limit).forEach { track ->
                        val detected = detectLanguage(track.name) ?: return@forEach
                        dao.updateLanguageByLocalId(track.localId, detected)
                }
        }

        private fun List<ListeningHistoryTrackEntity>.balancedBySource(limit: Int): List<ListeningHistoryTrackEntity> {
                val buckets = groupBy { it.sourceId ?: MANUAL_SOURCE_KEY }
                        .values
                        .map { it.toMutableList() }
                        .toMutableList()
                val result = mutableListOf<ListeningHistoryTrackEntity>()

                while (result.size < limit && buckets.isNotEmpty()) {
                        val iterator = buckets.iterator()
                        while (iterator.hasNext() && result.size < limit) {
                                val bucket = iterator.next()
                                result += bucket.removeAt(0)
                                if (bucket.isEmpty()) iterator.remove()
                        }
                }

                return result
        }

        private fun List<ListeningHistoryMusicTrack>.filterNonNative(): List<ListeningHistoryMusicTrack> =
                filter { it.language != null && it.language != "ru" }

        private companion object {
                const val MAX_ONBOARDING_SIZE = 35
                const val MANUAL_SOURCE_KEY = "__manual__"
                const val LYRICS_PREFETCH_STATUS_SYNCED = 2
                const val LYRICS_PREFETCH_STATUS_NOT_SYNCED = 3
        }
}
