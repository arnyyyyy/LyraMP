package com.arno.lyramp.feature.lyrics.domain


internal class GeniusHtmlParser {
        fun parse(html: String): String? {
                val processed = html
                        .replace("<br/>", "\n")
                        .replace("<br>", "\n")
                        .replace("<br />", "\n")

                val divContents = mutableListOf<String>()

                var searchIdx = 0
                while (searchIdx < processed.length) {
                        val attrIdx = processed.indexOf(
                                """data-lyrics-container="true"""",
                                searchIdx,
                                ignoreCase = true
                        )
                        if (attrIdx < 0) break

                        var tagStart = attrIdx
                        while (tagStart > 0 && processed[tagStart] != '<') tagStart--

                        if (!processed.startsWith("<div", tagStart, ignoreCase = true)) {
                                searchIdx = attrIdx + 1
                                continue
                        }

                        val openTagEnd = processed.indexOf('>', attrIdx)
                        if (openTagEnd < 0) {
                                searchIdx = attrIdx + 1; continue
                        }

                        val contentStart = openTagEnd + 1
                        val innerContent = extractLyricsContent(processed, contentStart)
                        val text = stripHtmlTags(innerContent)
                        if (text.isNotBlank()) divContents.add(text)

                        searchIdx = openTagEnd + 1
                }

                if (divContents.isEmpty()) return null

                var lyrics = divContents.joinToString("\n")
                lyrics = Regex("""\[.*?]""").replace(lyrics, "")
                lyrics = Regex("""\n{3,}""").replace(lyrics, "\n\n")
                return lyrics.trim('\n', ' ').takeIf { it.isNotBlank() }
        }


        private fun extractLyricsContent(html: String, startPos: Int): String {
                val sb = StringBuilder()
                var i = startPos
                var skipDivDepth = 0

                while (i < html.length) {
                        if (html[i] != '<') {
                                if (skipDivDepth == 0) sb.append(html[i])
                                i++
                                continue
                        }

                        if (html.startsWith("</div", i, ignoreCase = true)) {
                                val afterDiv = i + 5
                                if (afterDiv >= html.length || !html[afterDiv].isLetterOrDigit()) {
                                        if (skipDivDepth == 0) break
                                        skipDivDepth--
                                        val tagEnd = html.indexOf('>', i)
                                        i = if (tagEnd >= 0) tagEnd + 1 else html.length
                                        continue
                                }
                        }

                        if (html.startsWith("<div", i, ignoreCase = true)) {
                                val afterDiv = i + 4
                                if (afterDiv >= html.length || !html[afterDiv].isLetterOrDigit()) {
                                        val tagEnd = html.indexOf('>', i)
                                        if (tagEnd >= 0) {
                                                if (html[tagEnd - 1] != '/') skipDivDepth++
                                                i = tagEnd + 1
                                                continue
                                        }
                                }
                        }

                        val tagEnd = html.indexOf('>', i)
                        if (tagEnd >= 0) {
                                if (skipDivDepth == 0) sb.append(html.substring(i, tagEnd + 1))
                                i = tagEnd + 1
                        } else {
                                if (skipDivDepth == 0) sb.append(html[i])
                                i++
                        }
                }
                return sb.toString()
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
