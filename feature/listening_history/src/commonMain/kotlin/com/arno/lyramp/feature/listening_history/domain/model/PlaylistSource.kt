package com.arno.lyramp.feature.listening_history.domain.model

internal data class PlaylistSource(
        val id: String,
        val title: String,
        val url: String,
) {
        companion object {
                fun storedPlaylistId(url: String): String = "stored_playlist:$url"
        }
}
