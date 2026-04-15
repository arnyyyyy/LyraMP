package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack

internal class CompositeMusicService(private val services: List<MusicService>) : MusicService {
        override suspend fun getListeningHistory(limit: Int): List<ListeningHistoryMusicTrack> {
                val seen = mutableSetOf<String>()
                val merged = mutableListOf<ListeningHistoryMusicTrack>()

                for (service in services) {
                        val tracks = runCatching { service.getListeningHistory(limit) }.getOrDefault(emptyList())
                        for (track in tracks) {
                                val key = track.id ?: "${track.name}||${track.artists.joinToString(",")}"
                                if (seen.add(key)) merged.add(track)
                        }
                }
                return merged.take(limit)
        }
}
