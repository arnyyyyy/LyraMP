package com.arno.lyramp.feature.listening_history.data

internal data class TrackResolutionCandidate(
        val localId: Long,
        val name: String,
        val artists: List<String>,
)
