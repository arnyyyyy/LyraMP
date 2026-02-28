package com.arno.lyramp.feature.music_streaming.domain

import com.arno.lyramp.feature.music_streaming.model.StreamingTrackInfo

internal sealed class StreamingResult {
        data class Found(val info: StreamingTrackInfo) : StreamingResult()
        object NotFound : StreamingResult()
}
