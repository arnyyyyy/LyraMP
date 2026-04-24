package com.arno.lyramp.feature.listening_history.domain.model

internal data class PlaylistSource(
        val id: String,
        val title: String,
        val url: String,
) {
        companion object {
                const val OPTIONAL_PLAYLIST_ID = "optional_playlist"
                const val APPLE_PLAYLIST_ID = "apple_playlist"
        }
}
