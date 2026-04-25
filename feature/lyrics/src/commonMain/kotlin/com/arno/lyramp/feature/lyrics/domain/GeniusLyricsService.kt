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
                val normalizedArtist = normalizeString(artist)
                val normalizedTitle = normalizeString(title)
                val slug = "$normalizedArtist $normalizedTitle".replace("&", "and")
                        .replace(Regex("[^\\w\\s-]"), " ")
                        .trim()
                        .replace(Regex("\\s+"), "-")
                        .lowercase()
                        .replaceFirstChar { it.uppercaseChar() }
                return "https://genius.com/$slug-lyrics"
        }

        private fun normalizeString(input: String): String {
                return input
                        .replace("à", "a")
                        .replace("â", "a")
                        .replace("ä", "a")
                        .replace("ç", "c")
                        .replace("é", "e")
                        .replace("è", "e")
                        .replace("ê", "e")
                        .replace("ë", "e")
                        .replace("î", "i")
                        .replace("ï", "i")
                        .replace("ô", "o")
                        .replace("ö", "o")
                        .replace("ù", "u")
                        .replace("û", "u")
                        .replace("ü", "u")
                        .replace("ÿ", "y")
                        .replace("À", "A")
                        .replace("Â", "A")
                        .replace("Ä", "A")
                        .replace("Ç", "C")
                        .replace("É", "E")
                        .replace("È", "E")
                        .replace("Ê", "E")
                        .replace("Ë", "E")
                        .replace("Î", "I")
                        .replace("Ï", "I")
                        .replace("Ô", "O")
                        .replace("Ö", "O")
                        .replace("Ù", "U")
                        .replace("Û", "U")
                        .replace("Ü", "U")
                        .replace("Ÿ", "Y")
        }
}
