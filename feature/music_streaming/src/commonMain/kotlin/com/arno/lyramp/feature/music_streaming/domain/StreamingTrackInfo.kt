package com.arno.lyramp.feature.music_streaming.domain

data class StreamingTrackInfo(
        val url: String,
        val encryptionKey: String? = null,
        val transport: String? = null
)
