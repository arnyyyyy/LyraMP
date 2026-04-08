package com.arno.lyramp.feature.music_streaming.domain

import com.arno.lyramp.feature.music_streaming.model.StreamingTrackInfo
import com.arno.lyramp.util.Log

 class AppleStreamingService : StreamingService {
        override val serviceName: String = "Apple Music"

        override suspend fun getTrackStreamingInfo(trackId: String): StreamingTrackInfo? {
                Log.logger.w { "AppleStreamingService: Not implemented yet" }
                return null
        }
}
