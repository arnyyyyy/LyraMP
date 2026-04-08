package com.arno.lyramp.feature.lyrics.domain

 interface LyricsService {
        val serviceName: String

        suspend fun getLyrics(artist: String, song: String, trackId: String? = null): String?
        suspend fun getTimestampedLyrics(artist: String, song: String, trackId: String? = null): String? = null
}
