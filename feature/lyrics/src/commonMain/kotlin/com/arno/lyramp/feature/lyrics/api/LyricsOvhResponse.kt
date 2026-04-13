package com.arno.lyramp.feature.lyrics.api

import kotlinx.serialization.Serializable

@Serializable
internal data class LyricsOvhResponse(
        val lyrics: String?
)