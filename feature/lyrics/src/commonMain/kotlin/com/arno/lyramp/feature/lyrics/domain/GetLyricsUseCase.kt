package com.arno.lyramp.feature.lyrics.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetLyricsUseCase internal constructor(
        private val lyricsServiceFactory: LyricsServiceFactory
) {
        suspend operator fun invoke(artist: String, song: String, trackId: String? = null): LyricsResult =
                withContext(Dispatchers.Default) {
                        val primary = lyricsServiceFactory.getPrimaryService()
                        primary.getLyrics(artist, song, trackId)?.let { return@withContext LyricsResult.Found(it) }

                        val fallback = lyricsServiceFactory.getFallbackService()
                        fallback.getLyrics(artist, song, trackId)?.let { return@withContext LyricsResult.Found(it) }

                        LyricsResult.NotFound
                }
}

class GetTimestampedLyricsUseCase internal constructor(
        private val lyricsServiceFactory: LyricsServiceFactory
) {
        suspend operator fun invoke(artist: String, song: String, trackId: String? = null): LyricsResult =
                withContext(Dispatchers.Default) {
                        val primary = lyricsServiceFactory.getPrimaryService()

                        primary.getTimestampedLyrics(artist, song, trackId)
                                ?.takeIf { it.isNotBlank() }
                                ?.let { return@withContext LyricsResult.Found(it, isTimestamped = true) }

                        primary.getLyrics(artist, song, trackId)
                                ?.takeIf { it.isNotBlank() }
                                ?.let { return@withContext LyricsResult.Found(it, isTimestamped = false) }

                        val fallback = lyricsServiceFactory.getFallbackService()
                        fallback.getLyrics(artist, song, trackId)
                                ?.takeIf { it.isNotBlank() }
                                ?.let { return@withContext LyricsResult.Found(it, isTimestamped = false) }

                        LyricsResult.NotFound
                }
}
