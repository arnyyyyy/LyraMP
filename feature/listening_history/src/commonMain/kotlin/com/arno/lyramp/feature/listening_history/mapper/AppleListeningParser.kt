package com.arno.lyramp.feature.listening_history.mapper

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.util.Log
import com.fleeksoft.ksoup.Ksoup
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

internal class AppleMusicParser {

        private val json = Json { ignoreUnknownKeys = true; isLenient = true }

        fun parse(html: String): List<ListeningHistoryMusicTrack> {
                if (html.isBlank()) return emptyList()

                val image = extractOgImage(html)

                val fromJsonLd = parseFromJsonLd(html, image)
                if (fromJsonLd.isNotEmpty()) return fromJsonLd

                val fromLegacy = parseLegacy(html, image)
                return fromLegacy
        }

        private fun parseFromJsonLd(html: String, image: String?): List<ListeningHistoryMusicTrack> {
                val doc = runCatching { Ksoup.parse(html) }.getOrElse {
                        Log.logger.w(it) { "$TAG: KSoup failed to parse HTML" }
                        return emptyList()
                }

                val scripts = doc.select("script[type=application/ld+json]")
                if (scripts.isEmpty()) return emptyList()

                val tracks = mutableListOf<ListeningHistoryMusicTrack>()
                for (script in scripts) {
                        val raw = script.data().ifBlank { script.html() }
                        if (raw.isBlank()) continue

                        val root = runCatching { json.parseToJsonElement(raw) }.getOrNull() ?: continue
                        val obj = root as? JsonObject ?: continue

                        val trackArray = (obj["track"] as? JsonArray)
                                ?: (obj["@graph"] as? JsonArray)?.firstNotNullOfOrNull { (it as? JsonObject)?.get("track") as? JsonArray }
                                ?: continue

                        for (element in trackArray) {
                                val trackObj = element as? JsonObject ?: continue
                                val name = trackObj["name"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() } ?: continue

                                val artist = when (val by = trackObj["byArtist"]) {
                                        is JsonObject -> by["name"]?.jsonPrimitive?.contentOrNull
                                        is JsonArray -> by.firstNotNullOfOrNull {
                                                (it as? JsonObject)?.get("name")?.jsonPrimitive?.contentOrNull
                                        }

                                        else -> null
                                }

                                tracks += ListeningHistoryMusicTrack(
                                        name = name,
                                        artists = listOfNotNull(artist?.takeIf { it.isNotBlank() }),
                                        imageUrl = image,
                                )
                        }
                }
                return tracks
        }

        private fun parseLegacy(html: String, image: String?): List<ListeningHistoryMusicTrack> {
                val tracks = mutableListOf<ListeningHistoryMusicTrack>()
                val trackPattern = Regex(
                        """"id":"track-lockup[^"]*","title":"([^"]+)".*?"subtitleLinks":\s*\[\s*\{\s*"title":"([^"]+)""""
                )

                trackPattern.findAll(html).forEach { match ->
                        val name = decode(match.groupValues[1])
                        val artist = decode(match.groupValues[2])

                        tracks += ListeningHistoryMusicTrack(
                                name = name,
                                artists = listOfNotNull(artist),
                                imageUrl = image,
                        )
                }

                return tracks
        }

        private fun decode(text: String): String =
                """\\u([0-9a-fA-F]{4})""".toRegex().replace(text) {
                        it.groupValues[1].toInt(16).toChar().toString()
                }

        private fun extractOgImage(html: String): String? =
                """<meta\s+property="og:image"\s+content="([^"]+)""""
                        .toRegex().find(html)?.groupValues?.get(1)

        private companion object {
                const val TAG = "AppleMusicParser"
        }
}
