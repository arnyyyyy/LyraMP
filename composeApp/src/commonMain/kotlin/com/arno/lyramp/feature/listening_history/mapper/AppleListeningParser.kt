package com.arno.lyramp.feature.listening_history.mapper

import com.arno.lyramp.feature.listening_history.model.MusicTrack

internal class AppleMusicParser {
        fun parse(html: String): List<MusicTrack> {
                val tracks = mutableListOf<MusicTrack>()
                val image = extractOgImage(html)

                val trackPattern = Regex(
                        """"id":"track-lockup[^"]*","title":"([^"]+)".*?"subtitleLinks":\s*\[\s*\{\s*"title":"([^"]+)""""
                )

                trackPattern.findAll(html).forEach { match ->
                        val name = decode(match.groupValues[1])
                        val artist = decode(match.groupValues[2])

                        tracks += MusicTrack(
                                name = name,
                                artists = listOfNotNull(artist),
                                imageUrl = image
                        )
                }

                return tracks
        }

        private fun decode(text: String): String =
                """\\u([0-9a-fA-F]{4})""".toRegex().replace(text) {
                        it.groupValues[1].toInt(16).toChar().toString()
                }

        private fun extractOgImage(html: String): String? =
                """<meta\s+property="og:image"\s+content="([^"]+)""""
                        .toRegex().find(html)?.groupValues?.get(1)
}
