package com.arno.lyramp.feature.listening_history.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

@Serializable
internal data class AccountStatusResponse(
        val result: AccountResult?
)

@Serializable
internal data class AccountResult(
        val account: YandexAccount?,
        val plus: YandexPlus?
)

@Serializable
internal data class YandexPlus(
        @SerialName("has_plus")
        val hasPlus: Boolean = false
)


@Serializable
internal data class YandexAccount(
        val uid: String? = null,
        val login: String? = null,
        @SerialName("display_name")
        val displayName: String? = null,
        @SerialName("full_name")
        val fullName: String? = null
)


@Serializable
internal data class LikedTracksResponse(
        val result: LikedTracksResult?
)

@Serializable
internal data class LikedTracksResult(
        val library: YandexLibrary?
)

@Serializable
internal data class YandexLibrary(
        val tracks: List<YandexTrackItem>?
)

@Serializable
internal data class YandexTrackItem(
        val id: String? = null,
        @SerialName("albumId")
        val albumId: String? = null,
        val timestamp: String? = null,
        val track: YandexTrack? = null
)

@Serializable
internal data class YandexTrack(
        val id: String? = null,
        val title: String = "",
        val artists: List<YandexArtist>? = null,
        val albums: List<YandexAlbum>? = null
)

@Serializable
internal data class TracksResponseWrapper(
        val invocationInfo: YandexInvocationInfo? = null,
        val result: List<YandexTrack>? = null
)

@Serializable
internal data class YandexInvocationInfo(
        @SerialName("req-id")
        val reqId: String? = null,
        val hostname: String? = null,
        @SerialName("exec-duration-millis")
        val execDurationMillis: Int? = null
)
