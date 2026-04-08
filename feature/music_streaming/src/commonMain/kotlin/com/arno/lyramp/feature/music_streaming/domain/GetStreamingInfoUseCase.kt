package com.arno.lyramp.feature.music_streaming.domain

 class GetStreamingInfoUseCase(
        private val streamingServiceFactory: StreamingServiceFactory
) {
        suspend fun getStreamingInfo(trackId: String): StreamingResult {
                if (trackId.isBlank()) return StreamingResult.NotFound

                val service = streamingServiceFactory.getService()
                service.getTrackStreamingInfo(trackId)?.let {
                        return StreamingResult.Found(it)
                }

                return StreamingResult.NotFound
        }
}
