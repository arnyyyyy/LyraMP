package com.arno.lyramp.feature.listening_history.data

import com.russhwolf.settings.Settings
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class PlaylistSourcesRepository {
        private val settings = Settings()
        private val json = Json { ignoreUnknownKeys = true }

        fun getAll(): List<String> = decode(settings.getStringOrNull(PLAYLIST_SOURCES_KEY))

        fun add(url: String) {
                val cleanUrl = url.trim().takeIf { it.isNotBlank() } ?: return
                val updated = (getAll() + cleanUrl).distinct()
                settings.putString(PLAYLIST_SOURCES_KEY, json.encodeToString(PlaylistSourcesPayload(updated)))
        }

        fun remove(url: String) {
                val updated = getAll().filterNot { it == url }
                if (updated.isEmpty()) {
                        settings.remove(PLAYLIST_SOURCES_KEY)
                } else {
                        settings.putString(PLAYLIST_SOURCES_KEY, json.encodeToString(PlaylistSourcesPayload(updated)))
                }
        }

        private fun decode(raw: String?): List<String> {
                if (raw.isNullOrBlank()) return emptyList()
                return runCatching {
                        json.decodeFromString<PlaylistSourcesPayload>(raw).urls
                }.getOrDefault(emptyList())
        }

        @Serializable
        private data class PlaylistSourcesPayload(val urls: List<String>)

        private companion object {
                const val PLAYLIST_SOURCES_KEY = "listening_history_playlist_sources"
        }
}
