package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

// NB : агрегатор
internal class CompositeMusicService(private val services: List<MusicService>) : MusicService {
        override suspend fun getListeningHistory(limit: Int?): List<ListeningHistoryMusicTrack> = coroutineScope {
                val results = services.map { service -> async { loadService(service, limit) } }.awaitAll()

                val seen = mutableSetOf<String>()
                val merged = mutableListOf<ListeningHistoryMusicTrack>()

                for (result in results) {
                        val tracks = result.tracks
                        for (track in tracks) {
                                val key = track.id ?: "${track.name}||${track.artists.joinToString(",")}"
                                if (seen.add(key)) merged.add(track)
                        }
                }

                val successfulLoads = results.count { it.error == null }
                if (successfulLoads == 0) {
                        results.firstNotNullOfOrNull { it.error }?.let { throw it }
                }
                if (limit != null) merged.take(limit) else merged
        }

        private suspend fun loadService(service: MusicService, limit: Int?): ServiceLoadResult {
                return try {
                        ServiceLoadResult(tracks = service.getListeningHistory(limit), error = null)
                } catch (ce: CancellationException) {
                        throw ce
                } catch (e: Throwable) {
                        Log.logger.e(e) { "Error fetching listening history from ${service::class.simpleName}" }
                        ServiceLoadResult(tracks = emptyList(), error = e)
                }
        }

        private data class ServiceLoadResult(
                val tracks: List<ListeningHistoryMusicTrack>,
                val error: Throwable?,
        )
}
