package com.arno.lyramp.feature.lyrics.domain

import com.arno.lyramp.feature.lyrics.api.LyricsOvhApi
import com.arno.lyramp.util.Log

internal class LyricsOvhService(private val lyricsOvhApi: LyricsOvhApi) : LyricsService {
        override val serviceName: String = "Lyrics.ovh"

        override suspend fun getLyrics(artist: String, song: String, trackId: String?): String? {
                if (artist.isBlank() || song.isBlank()) return null

                return runCatching { lyricsOvhApi.getLyrics(artist.trim(), song.trim()) }
                        .onFailure { Log.logger.e(it) { "LyricsOvhService: Failed to fetch lyrics" } }
                        .getOrNull()
        }
}
