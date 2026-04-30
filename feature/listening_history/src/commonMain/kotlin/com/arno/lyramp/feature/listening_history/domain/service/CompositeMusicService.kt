package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log

internal class CompositeMusicService(private val services: List<MusicService>) : MusicService {
        override suspend fun getListeningHistory(limit: Int?): List<ListeningHistoryMusicTrack> {
                val seen = mutableSetOf<String>()
                val merged = mutableListOf<ListeningHistoryMusicTrack>()
                var successfulLoads = 0
                var firstError: Throwable? = null

                for (service in services) {
                        val result = runCatching { service.getListeningHistory(limit) }
                        result.onFailure { e ->
                                if (firstError == null) firstError = e
                                Log.logger.e(e) { "Error fetching listening history from ${service::class.simpleName}" }
                        }
                        result.onSuccess { successfulLoads++ }
                        val tracks = result.getOrDefault(emptyList())
                        for (track in tracks) {
                                val key = track.id ?: "${track.name}||${track.artists.joinToString(",")}"
                                if (seen.add(key)) merged.add(track)
                        }
                }
                if (successfulLoads == 0) {
                        firstError?.let { throw it }
                }
                return if (limit != null) merged.take(limit) else merged
        }
}
