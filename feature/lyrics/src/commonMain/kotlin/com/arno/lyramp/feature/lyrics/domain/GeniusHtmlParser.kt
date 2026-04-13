package com.arno.lyramp.feature.lyrics.domain

internal class GeniusHtmlParser {

        fun parse(html: String): String? {
                val processed = html.replace("<br/>", "\n")
                        .replace("<br>", "\n")
                        .replace("<br />", "\n")

                val divContents = mutableListOf<String>()

                Regex(
                        """(?si)<div[^>]*class="[^"]*(?:lyrics$|Lyrics__Container)[^"]*"[^>]*>(.*?)</div>"""
                ).findAll(processed).forEach { match ->
                        val innerHtml = match.groupValues[1]
                        val text = stripHtmlTags(innerHtml)
                        if (text.isNotBlank()) divContents.add(text)
                }

                if (divContents.isEmpty()) return null

                var lyrics = divContents.joinToString("\n")

                lyrics = Regex("""\[.*?]""").replace(lyrics, "")
                lyrics = Regex("""\n{3,}""").replace(lyrics, "\n")

                return lyrics.trim('\n', ' ').takeIf { it.isNotBlank() }
        }

        private fun stripHtmlTags(html: String): String {
                return html
                        .replace(Regex("""<[^>]+>"""), "")
                        .replace("&amp;", "&")
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replace("&quot;", "\"")
                        .replace("&#x27;", "'")
                        .replace("&#39;", "'")
                        .replace("&nbsp;", " ")
        }
}
