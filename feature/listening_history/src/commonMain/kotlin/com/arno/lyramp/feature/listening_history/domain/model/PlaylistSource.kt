package com.arno.lyramp.feature.listening_history.domain.model

internal data class PlaylistSource(
        val id: String,
        val title: String,
        val url: String,
) {
        companion object {
                const val LEGACY_OPTIONAL_PLAYLIST_ID = "legacy_optional_playlist"
                const val APPLE_PLAYLIST_ID = "apple_playlist"

                fun storedPlaylistId(url: String): String = "stored_playlist:$url"
        }
}
