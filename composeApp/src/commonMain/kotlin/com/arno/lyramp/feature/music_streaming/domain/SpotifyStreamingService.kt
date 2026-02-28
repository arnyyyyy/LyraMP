package com.arno.lyramp.feature.music_streaming.domain

import com.arno.lyramp.feature.music_streaming.model.StreamingTrackInfo
import com.arno.lyramp.util.Log

internal class SpotifyStreamingService : StreamingService {
        override val serviceName: String = "Spotify"

        override suspend fun getTrackStreamingInfo(trackId: String): StreamingTrackInfo? {
                Log.logger.w { "SpotifyStreamingService: Not implemented yet" }
                return null
        }
}

