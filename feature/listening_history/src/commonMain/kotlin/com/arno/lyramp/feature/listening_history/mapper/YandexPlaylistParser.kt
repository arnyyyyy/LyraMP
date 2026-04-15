package com.arno.lyramp.feature.listening_history.mapper

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class YandexPlaylistParser {
        private val json = Json { ignoreUnknownKeys = true; isLenient = true }

        fun parse(html: String): List<ListeningHistoryMusicTrack> {
                val playlist = extractPlaylist(html) ?: return emptyList()
                return playlist.mapNotNull { parseTrackItem(it) }
        }

        private fun extractPlaylist(html: String): List<JsonObject>? {
                var index = html.indexOf(".push({")
                while (index >= 0) {
                        val jsonStr = extractJsonObject(html, index + 6)
                        if (jsonStr != null) {
                                val tracks = runCatching {
                                        val obj = json.parseToJsonElement(jsonStr).jsonObject
                                        val playlistObj = obj["playlist"]?.jsonObject ?: return@runCatching null
                                        val items = playlistObj["tracks"]?.jsonArray ?: return@runCatching null
                                        items.mapNotNull { item ->
                                                val data = item.jsonObject["track"]?.jsonObject
                                                data
                                        }
                                }.getOrNull()

                                if (!tracks.isNullOrEmpty()) return tracks
                        }
                        index = html.indexOf(".push({", index + 1)
                }
                return null
        }

        private fun parseTrackItem(trackObj: JsonObject): ListeningHistoryMusicTrack? {
                val title = trackObj["title"]?.jsonPrimitive?.content ?: return null
                val artists = trackObj["artists"]?.jsonArray?.mapNotNull {
                        it.jsonObject["name"]?.jsonPrimitive?.content
                }.orEmpty()
                val coverUri = trackObj["coverUri"]?.jsonPrimitive?.content
                val albumName = trackObj["albums"]?.jsonArray?.firstOrNull()
                        ?.jsonObject?.get("title")?.jsonPrimitive?.content
                val trackId = trackObj["id"]?.jsonPrimitive?.content

                return ListeningHistoryMusicTrack(
                        id = trackId,
                        name = title,
                        artists = artists,
                        albumName = albumName,
                        imageUrl = coverUri?.let { "https://${it.replace("%%", "200x200")}" }
                )
        }

        private fun extractJsonObject(text: String, start: Int): String? {
                if (start >= text.length || text[start] != '{') return null
                var depth = 0
                var inString = false
                var escape = false

                for (i in start until text.length) {
                        val ch = text[i]
                        when {
                                escape -> escape = false
                                ch == '\\' -> escape = true
                                ch == '"' -> inString = !inString
                                !inString && ch == '{' -> depth++
                                !inString && ch == '}' -> if (--depth == 0) return text.substring(start, i + 1)
                        }
                }
                return null
        }
}
