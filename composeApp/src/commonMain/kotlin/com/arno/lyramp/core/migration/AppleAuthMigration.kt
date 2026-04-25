package com.arno.lyramp.core.migration

import com.russhwolf.settings.Settings
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object AppleAuthMigration {

        private const val APPLE_PLAYLIST_URL_KEY = "apple_playlist_url"
        private const val GENERIC_PLAYLIST_URL_KEY = "generic_playlist_url"
        private const val LAST_SERVICE_KEY = "last_authorized_service"
        private const val PLAYLIST_SOURCES_KEY = "listening_history_playlist_sources"
        private const val MIGRATION_FLAG_KEY = "apple_auth_migrated_v1"

        private const val APPLE = "APPLE"
        private const val NONE = "NONE"

        private val json = Json { ignoreUnknownKeys = true }

        fun runIfNeeded() {
                val settings = Settings()
                if (settings.getBooleanOrNull(MIGRATION_FLAG_KEY) == true) return

                val legacyUrls = listOfNotNull(
                        settings.getStringOrNull(APPLE_PLAYLIST_URL_KEY)?.takeIf { it.isNotBlank() },
                        settings.getStringOrNull(GENERIC_PLAYLIST_URL_KEY)?.takeIf { it.isNotBlank() },
                )

                if (legacyUrls.isNotEmpty()) {
                        val current = decodeUrls(settings.getStringOrNull(PLAYLIST_SOURCES_KEY))
                        val merged = (current + legacyUrls).distinct()
                        if (merged != current) {
                                settings.putString(
                                        PLAYLIST_SOURCES_KEY,
                                        json.encodeToString(PlaylistSourcesPayload(merged))
                                )
                        }
                        settings.remove(APPLE_PLAYLIST_URL_KEY)
                        settings.remove(GENERIC_PLAYLIST_URL_KEY)
                }

                if (settings.getStringOrNull(LAST_SERVICE_KEY) == APPLE) {
                        settings.putString(LAST_SERVICE_KEY, NONE)
                }

                settings.putBoolean(MIGRATION_FLAG_KEY, true)
        }

        private fun decodeUrls(raw: String?): List<String> {
                if (raw.isNullOrBlank()) return emptyList()
                return runCatching {
                        json.decodeFromString<PlaylistSourcesPayload>(raw).urls
                }.getOrDefault(emptyList())
        }

        @Serializable
        private data class PlaylistSourcesPayload(val urls: List<String>)
}
