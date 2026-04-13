package com.arno.lyramp.feature.lyrics.domain

internal class LyricsTextParser {
        fun parse(lyrics: String): List<List<String>> =
                lyrics.split("\n").map { line ->
                        if (line.isBlank()) emptyList()
                        else line.split(Regex("\\s+")).filter { it.isNotEmpty() }
                }
}
