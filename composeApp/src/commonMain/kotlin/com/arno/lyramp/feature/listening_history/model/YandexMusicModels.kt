package com.arno.lyramp.feature.listening_history.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject


@Serializable
internal data class YandexPlaylist(
        val uuid: String? = null,
        val meta: JsonObject? = null,
        val items: List<YandexPlaylistItem>? = null
)

@Serializable
internal data class YandexPlaylistItem(
        val id: String? = null,
        val albumId: Long? = null,
        val key: String? = null,
        val data: YandexTrackData? = null
)

@Serializable
internal data class YandexTrackData(
        val id: String? = null,
        val title: String? = null,
        val coverUri: String? = null,
        val artists: List<YandexArtist>? = null,
        val albums: List<YandexAlbum>? = null
) {
        fun toTrack() = title?.let { t ->
                MusicTrack(
                        name = t,
                        artists = artists?.mapNotNull { it.name }.orEmpty(),
                        albumName = albums?.firstOrNull()?.title,
                        imageUrl = coverUri
                )
        }
}

@Serializable
internal data class YandexArtist(
        val id: String? = null,
        val name: String? = null
)

@Serializable
internal data class YandexAlbum(
        val id: Long? = null,
        val title: String? = null
)
