package com.arno.lyramp.feature.listening_history.model

internal fun ListeningHistoryMusicTrack.stableKey(): String =
        id?.takeIf { it.isNotBlank() } ?: "$name||${artists.joinToString(",")}"
