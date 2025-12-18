package com.arno.lyramp.feature.listening_history.domain

import com.arno.lyramp.feature.authorization.repository.AppleAuthRepository
import com.arno.lyramp.feature.listening_history.model.MusicTrack
import com.arno.lyramp.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AppleMusicService(
        private val authRepo: AppleAuthRepository,
        private val httpClient: HttpClient
) : MusicService {

        override suspend fun getListeningHistory(limit: Int): List<MusicTrack> {
                return withContext(Dispatchers.Default) {
                        val url = authRepo.getPlaylistUrl() ?: return@withContext emptyList()

                        runCatching {
                                val body: String = httpClient.get(url) {
                                        header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
                                        header("Accept", "text/html,application/xhtml+xml")
                                        header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                                }.body()

                                Log.logger.d { "AppleMusicService: fetched HTML, length: ${body.length}" }
                                parseApplePlaylist(body).take(limit)
                        }
                                .onFailure { Log.logger.e(it) { "AppleMusicService: failed to fetch playlist" } }
                                .getOrDefault(emptyList())
                }
        }

        private fun parseApplePlaylist(html: String): List<MusicTrack> {
                val tracks = mutableListOf<MusicTrack>()

                val playlistImage = extractOgImage(html)
                val trackPattern = Regex(""""id":"track-lockup[^"]*","title":"([^"]+)".*?"subtitleLinks":\s*\[\s*\{\s*"title":"([^"]+)"""")

                trackPattern.findAll(html).forEach { match ->
                        val trackName = match.groupValues.getOrNull(1)?.let { decodeUnicode(it) }
                        val artistName = match.groupValues.getOrNull(2)?.let { decodeUnicode(it) }

                        if (trackName != null) {
                                tracks.add(
                                        MusicTrack(
                                                name = trackName,
                                                artists = listOfNotNull(artistName),
                                                albumName = null,
                                                imageUrl = playlistImage
                                        )
                                )
                        }
                }

                Log.logger.d { "AppleMusicService: parsed ${tracks.size} tracks" }
                return tracks
        }

        private fun decodeUnicode(text: String): String {
                val pattern = """\\u([0-9a-fA-F]{4})""".toRegex()
                return pattern.replace(text) { matchResult ->
                        val code = matchResult.groupValues[1].toInt(16)
                        code.toChar().toString()
                }
        }

        private fun extractOgImage(html: String): String? {
                val pattern = """<meta\s+property="og:image"\s+content="([^"]+)"""".toRegex()
                return pattern.find(html)?.groupValues?.getOrNull(1)
        }
}
