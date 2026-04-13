package com.arno.lyramp.feature.lyrics.domain

import com.arno.lyramp.feature.lyrics.api.GeniusLyricsApi
import com.arno.lyramp.util.Log

internal class GeniusLyricsService(
        private val api: GeniusLyricsApi,
        private val parser: GeniusHtmlParser
) : LyricsService {
        override val serviceName: String = "Genius"

        override suspend fun getLyrics(artist: String, song: String, trackId: String?): String? {
                if (artist.isBlank() || song.isBlank()) return null

                return runCatching {
                        val url = buildGeniusUrl(artist.trim(), song.trim())
                        val html = api.fetchHtml(url) ?: return null
                        parser.parse(html)
                }
                        .onFailure { Log.logger.e(it) { "GeniusLyricsService: Failed to fetch lyrics" } }
                        .getOrNull()
        }

        private fun buildGeniusUrl(artist: String, title: String): String {
                val slug = "$artist $title".replace("&", "and")
                        .replace(Regex("[^\\w\\s-]"), " ")
                        .trim()
                        .replace(Regex("\\s+"), "-")
                        .lowercase()
                        .replaceFirstChar { it.uppercaseChar() }
                return "https://genius.com/$slug-lyrics"
        }
}
