package com.arno.lyramp.feature.music_streaming.model

import com.arno.lyramp.feature.listening_history.model.YandexInvocationInfo
import kotlinx.serialization.Serializable

internal data class StreamingTrackInfo(
        val url: String,
        val encryptionKey: String? = null,
        val transport: String? = null
)

@Serializable
internal data class TrackDownloadInfoResponse(
        val result: TrackDownloadInfoResult? = null,
        val invocationInfo: YandexInvocationInfo? = null
)

@Serializable
internal data class TrackDownloadInfoResult(
        val downloadInfo: DownloadInfo? = null
)

@Serializable
data class DownloadInfo(
        val url: String? = null,
        val key: String? = null,
        val codec: String? = null,
        val bitrate: Int? = null,
        val urls: List<String>? = null,
        val trackId: String? = null,
        val quality: String? = null,
        val transport: String? = null,
        val size: Long? = null,
        val gain: Boolean? = null
)