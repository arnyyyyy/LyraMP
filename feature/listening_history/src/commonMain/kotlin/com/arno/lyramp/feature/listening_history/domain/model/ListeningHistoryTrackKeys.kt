package com.arno.lyramp.feature.listening_history.model

internal fun ListeningHistoryMusicTrack.stableKey(): String =
        id?.takeIf { it.isNotBlank() } ?: "$name||${artists.joinToString(",")}"

fun ListeningHistoryMusicTrack.hasResolvedYandexTrackId(): Boolean = id.isResolvedYandexTrackId()

fun String?.isResolvedYandexTrackId(): Boolean = !isNullOrBlank() && !contains(SYNTHETIC_TRACK_ID_DELIMITER)

private const val SYNTHETIC_TRACK_ID_DELIMITER = "||"
