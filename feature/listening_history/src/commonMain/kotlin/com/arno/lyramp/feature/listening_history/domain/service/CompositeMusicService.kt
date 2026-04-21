package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log

internal class CompositeMusicService(private val services: List<MusicService>) : MusicService {
        override suspend fun getListeningHistory(limit: Int): List<ListeningHistoryMusicTrack> {
                val seen = mutableSetOf<String>()
                val merged = mutableListOf<ListeningHistoryMusicTrack>()

                for (service in services) {
                        val result = runCatching { service.getListeningHistory(Int.MAX_VALUE) }
                        result.onFailure { e ->
                                Log.logger.e(e) { "Error fetching listening history from ${service::class.simpleName}" }
                        }
                        val tracks = result.getOrDefault(emptyList())
                        for (track in tracks) {
                                val key = track.id ?: "${track.name}||${track.artists.joinToString(",")}"
                                if (seen.add(key)) merged.add(track)
                        }
                }
                return merged
        }
}
