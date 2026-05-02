package com.arno.lyramp.feature.music_streaming.domain

interface StreamingService {
        val serviceName: String
        suspend fun getTrackStreamingInfo(trackId: String): StreamingTrackInfo?
}
