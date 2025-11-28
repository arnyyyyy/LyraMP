package com.arno.lyramp.feature.lyrics.model

data class LyricData(
        val artist: String,
        val name: String,
        val lyrics: String,
        val album: String? = null,
        val albumArt: String? = null,
)