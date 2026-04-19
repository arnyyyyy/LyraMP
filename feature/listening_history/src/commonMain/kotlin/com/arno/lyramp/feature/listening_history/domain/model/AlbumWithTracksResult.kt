package com.arno.lyramp.feature.listening_history.domain.model

data class AlbumWithTracksResult(
        val albumId: String,
        val title: String,
        val artistName: String,
        val coverUri: String?,
        val tracks: List<TrackResult>
) {
        data class TrackResult(
                val trackIndex: Int,
                val trackId: String,
                val title: String,
                val artists: String
        )
}
