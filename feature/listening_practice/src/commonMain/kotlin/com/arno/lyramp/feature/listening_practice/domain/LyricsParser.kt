package com.arno.lyramp.feature.listening_practice.domain

import com.arno.lyramp.feature.listening_practice.model.LyricLine

internal object LyricsParser {
        private val LRC_PATTERN = Regex("""^\[(\d{2}):(\d{2})\.(\d{2,3})]\s*(.*)$""")

        fun parseLrc(lyrics: String): List<LyricLine>? {
                val parsed = lyrics.lines().mapNotNull { line ->
                        val match = LRC_PATTERN.matchEntire(line.trim()) ?: return@mapNotNull null
                        val minutes = match.groupValues[1].toLong()
                        val seconds = match.groupValues[2].toLong()
                        val centisStr = match.groupValues[3]
                        val centisMs = if (centisStr.length == 2) centisStr.toLong() * 10 else centisStr.toLong()
                        val startMs = (minutes * 60 + seconds) * 1000 + centisMs
                        val text = match.groupValues[4].trim()
                        if (text.isBlank()) return@mapNotNull null
                        startMs to text
                }
                if (parsed.isEmpty()) return null
                return parsed.mapIndexed { index, (startMs, text) ->
                        val endMs = if (index + 1 < parsed.size) parsed[index + 1].first else startMs + 5000
                        LyricLine(index = index, text = text, startMs = startMs, endMs = endMs)
                }
        }

        fun parsePlain(lyrics: String): List<LyricLine> {
                return lyrics.lines().mapIndexedNotNull { index, line ->
                        line.trim().takeIf { it.isNotBlank() }?.let { LyricLine(index = index, text = it) }
                }
        }
}
