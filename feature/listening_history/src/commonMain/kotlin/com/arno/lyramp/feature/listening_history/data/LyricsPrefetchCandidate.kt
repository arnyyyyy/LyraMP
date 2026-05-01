package com.arno.lyramp.feature.listening_history.data

internal data class LyricsPrefetchCandidate(
        val localId: Long,
        val name: String,
        val artist: String,
        val trackId: String?,
)
