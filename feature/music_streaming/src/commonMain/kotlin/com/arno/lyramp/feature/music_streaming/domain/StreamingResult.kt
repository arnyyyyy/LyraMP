package com.arno.lyramp.feature.music_streaming.domain

sealed interface StreamingResult {
        data class Found(val info: StreamingTrackInfo) : StreamingResult
        data object NotFound : StreamingResult
}
