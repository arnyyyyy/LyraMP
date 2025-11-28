package com.arno.lyramp.feature.lyrics.model

import kotlinx.serialization.Serializable

@Serializable
data class LyricsOvhResponse(
        val lyrics: String?
)