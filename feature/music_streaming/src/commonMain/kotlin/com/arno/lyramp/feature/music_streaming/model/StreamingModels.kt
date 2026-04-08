package com.arno.lyramp.feature.music_streaming.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class StreamingTrackInfo(
        val url: String,
        val encryptionKey: String? = null,
        val transport: String? = null
)

@Serializable
data class TrackDownloadInfoResponse(
        val result: TrackDownloadInfoResult? = null,
        val invocationInfo: StreamingInvocationInfo? = null
)

@Serializable
data class StreamingInvocationInfo(
        @SerialName("req-id")
        val reqId: String? = null,
        val hostname: String? = null,
        @SerialName("exec-duration-millis")
        val execDurationMillis: Int? = null
)

@Serializable
data class TrackDownloadInfoResult(
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