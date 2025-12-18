//package com.arno.lyramp.feature.extra
//
//import com.arno.lyramp.feature.authorization.repository.YandexAuthRepository
//import com.arno.lyramp.feature.listening_history.domain.MusicService
//import com.arno.lyramp.feature.listening_history.model.MusicTrack
//import com.arno.lyramp.feature.listening_history.model.YandexPlaylist
//import com.arno.lyramp.util.Log
//import io.ktor.client.HttpClient
//import io.ktor.client.call.body
//import io.ktor.client.request.get
//import io.ktor.client.request.header
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.json.jsonObject
//
//internal class YandexPlaylistMusicService(
//        private val authRepo: YandexPlaylistAuthRepository,
//        private val httpClient: HttpClient,
//        private val json: Json = Json { ignoreUnknownKeys = true; isLenient = true }
//) : MusicService {
//
//        override suspend fun getListeningHistory(limit: Int): List<MusicTrack> {
//                return withContext(Dispatchers.Default) {
//                        val url = authRepo.getPlaylistUrl() ?: return@withContext emptyList()
//
//                        runCatching {
//                                val body: String = httpClient.get(url) {
//                                        header("User-Agent", "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36")
//                                        header("Accept", "text/html")
//                                }.body()
//
//                                val playlist = extractPlaylist(body) ?: return@withContext emptyList()
//
//                                playlist.items.orEmpty()
//                                        .mapNotNull { it.data?.toTrack() }
//                                        .take(limit)
//                        }
//                                .onFailure { Log.logger.e(it) { "YandexMusicService: failed" } }
//                                .getOrDefault(emptyList())
//                }
//        }
//
//        override fun isAuthorized(): Boolean = authRepo.hasPlaylist()
//
//        private fun extractPlaylist(html: String): YandexPlaylist? {
//                var index = html.indexOf(".push({")
//                while (index >= 0) {
//                        val jsonStr = extractJsonObject(html, index + 6)
//                        if (jsonStr != null) {
//                                val playlist = runCatching {
//                                        val obj = json.parseToJsonElement(jsonStr).jsonObject
//                                        obj["playlist"]?.let {
//                                                json.decodeFromJsonElement(YandexPlaylist.serializer(), it)
//                                        }
//                                }.getOrNull()
//
//                                if (playlist?.items?.any { it.data != null } == true) {
//                                        return playlist
//                                }
//                        }
//
//                        index = html.indexOf(".push({", index + 1)
//                }
//
//                return null
//        }
//
//        private fun extractJsonObject(text: String, start: Int): String? {
//                if (start >= text.length || text[start] != '{') return null
//                var depth = 0
//                var inString = false
//                var escape = false
//
//                for (i in start until text.length) {
//                        val ch = text[i]
//                        when {
//                                escape -> escape = false
//                                ch == '\\' -> escape = true
//                                ch == '"' -> inString = !inString
//                                !inString && ch == '{' -> depth++
//                                !inString && ch == '}' -> if (--depth == 0) return text.substring(start, i + 1)
//                        }
//                }
//                return null
//        }
//}
