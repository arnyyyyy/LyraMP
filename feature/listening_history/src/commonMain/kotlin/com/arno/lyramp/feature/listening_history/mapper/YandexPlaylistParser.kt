package com.arno.lyramp.feature.listening_history.mapper

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

internal data class PlaylistOwnerInfo(val uid: Long, val kind: Long)

internal class YandexPlaylistParser {
        private val json = Json { ignoreUnknownKeys = true; isLenient = true }

        fun parse(html: String): List<ListeningHistoryMusicTrack> {
                val playlist = extractPlaylist(html)
                if (playlist == null) {
                        return emptyList()
                }
                val tracks = playlist.mapNotNull { parseTrackItem(it) }
                Log.logger.i { "AAAAYandexPlaylistParser: parsed ${tracks.size} tracks out of ${playlist.size} items" }
                return tracks
        }

        fun extractOwnerInfo(html: String): PlaylistOwnerInfo? {
                var index = html.indexOf(NEW_FORMAT_MARKER)
                while (index >= 0) {
                        val objStart = index + NEW_FORMAT_OBJECT_OFFSET
                        val jsonStr = extractJsonObject(html, objStart)
                        if (jsonStr != null) {
                                val info = runCatching {
                                        val obj = json.parseToJsonElement(jsonStr).jsonObject
                                        val meta = obj["meta"]?.jsonObject ?: return@runCatching null
                                        val uid = meta["uid"]?.jsonPrimitive?.longOrNull ?: return@runCatching null
                                        val kind = meta["kind"]?.jsonPrimitive?.longOrNull ?: return@runCatching null
                                        PlaylistOwnerInfo(uid, kind)
                                }.getOrNull()
                                if (info != null) return info
                        }
                        index = html.indexOf(NEW_FORMAT_MARKER, index + 1)
                }
                return null
        }

        private fun extractPlaylist(html: String): List<JsonObject>? {
                extractFromNewFormat(html)?.let { return it }
                return extractFromLegacyFormat(html)
        }

        private fun extractFromNewFormat(html: String): List<JsonObject>? {
                var index = html.indexOf(NEW_FORMAT_MARKER)
                var matches = 0
                while (index >= 0) {
                        matches++
                        val objStart = index + NEW_FORMAT_OBJECT_OFFSET
                        val jsonStr = extractJsonObject(html, objStart)
                        if (jsonStr != null) {
                                val items = runCatching {
                                        val playlistObj = json.parseToJsonElement(jsonStr).jsonObject
                                        val arr = playlistObj["items"]?.jsonArray
                                        arr?.mapNotNull { item ->
                                                val data = item.jsonObject["data"]
                                                if (data != null && data !is JsonNull) data.jsonObject else null
                                        }
                                }.onFailure {
                                        Log.logger.e(it) { "YandexPlaylistParser: failed to parse new-format JSON (match #$matches)" }
                                }.getOrNull()

                                if (!items.isNullOrEmpty()) return items
                        }
                        index = html.indexOf(NEW_FORMAT_MARKER, index + 1)
                }
                return null
        }

        private fun extractFromLegacyFormat(html: String): List<JsonObject>? {
                var index = html.indexOf(LEGACY_FORMAT_MARKER)
                while (index >= 0) {
                        val jsonStr = extractJsonObject(html, index + LEGACY_FORMAT_MARKER.length - 1)
                        if (jsonStr != null) {
                                val tracks = runCatching {
                                        val obj = json.parseToJsonElement(jsonStr).jsonObject
                                        val playlistObj = obj["playlist"]?.jsonObject
                                                ?: return@runCatching null
                                        val items = playlistObj["tracks"]?.jsonArray
                                                ?: return@runCatching null
                                        items.mapNotNull { it.jsonObject["track"]?.jsonObject }
                                }.getOrNull()

                                if (!tracks.isNullOrEmpty()) return tracks
                        }
                        index = html.indexOf(LEGACY_FORMAT_MARKER, index + 1)
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
                if (start < 0 || start >= text.length || text[start] != '{') return null
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

        private companion object {
                const val NEW_FORMAT_MARKER = "\"playlist\":{\"uuid\""
                const val NEW_FORMAT_OBJECT_OFFSET = 11
                const val LEGACY_FORMAT_MARKER = ".push({"
        }
}
