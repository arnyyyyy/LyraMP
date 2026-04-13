package com.arno.lyramp.feature.lyrics.api

import kotlinx.serialization.Serializable

@Serializable
internal data class YandexLyricsResponse(
        val result: YandexLyricsResult? = null
)

@Serializable
internal data class YandexLyricsResult(
        val downloadUrl: String? = null,
        val lyricId: Int? = null,
        val externalLyricId: String? = null,
        val writers: List<String>? = null
)
