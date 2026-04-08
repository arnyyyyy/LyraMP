package com.arno.lyramp.feature.music_streaming.domain

import com.arno.lyramp.feature.music_streaming.model.StreamingTrackInfo

 interface StreamingService {
        val serviceName: String

        suspend fun getTrackStreamingInfo(trackId: String): StreamingTrackInfo?
}
