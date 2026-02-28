package com.arno.lyramp.feature.lyrics.domain

internal class LyricsUseCase(
        private val lyricsServiceFactory: LyricsServiceFactory
) {
        suspend fun getTimeStampedLyrics(artist: String, song: String, trackId: String? = null): LyricsResult {
                val primary = lyricsServiceFactory.getPrimaryService()

                primary.getTimestampedLyrics(artist, song, trackId)
                        ?.takeIf { it.isNotBlank() }
                        ?.let { return LyricsResult.Found(it, isTimestamped = true) }

                primary.getLyrics(artist, song, trackId)
                        ?.takeIf { it.isNotBlank() }
                        ?.let { return LyricsResult.Found(it, isTimestamped = false) }

                val fallback = lyricsServiceFactory.getFallbackService()
                fallback.getLyrics(artist, song, trackId)
                        ?.takeIf { it.isNotBlank() }
                        ?.let { return LyricsResult.Found(it, isTimestamped = false) }

                return LyricsResult.NotFound
        }

        suspend fun getLyrics(artist: String, song: String, trackId: String? = null): LyricsResult {
                val primary = lyricsServiceFactory.getPrimaryService()
                primary.getLyrics(artist, song, trackId)?.let { return LyricsResult.Found(it) }

                val fallback = lyricsServiceFactory.getFallbackService()
                fallback.getLyrics(artist, song, trackId)?.let { return LyricsResult.Found(it) }

                return LyricsResult.NotFound
        }
}
