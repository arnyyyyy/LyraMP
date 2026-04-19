package com.arno.lyramp.feature.lyrics.domain

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode

internal class GeniusHtmlParser {
        fun parse(html: String): String? {
                val doc = Ksoup.parse(html)
                val containers = doc.select("div[data-lyrics-container=true]")
                if (containers.isEmpty()) return null

                val rendered = containers.joinToString("\n") {
                        val sb = StringBuilder()
                        appendNode(it, sb)
                        sb.toString()
                }

                val cleaned = SECTION_TAG_REGEX.replace(rendered, "")
                val withoutPreamble = stripPreamble(cleaned)
                val collapsed = MULTI_NEWLINE_REGEX.replace(withoutPreamble, "\n\n")
                return collapsed.trim('\n', ' ').takeIf { it.isNotBlank() }
        }

        private fun stripPreamble(text: String): String {
                val idx = text.indexOf("Lyrics")
                if (idx < 0) return text
                if (idx > 600) return text
                val afterMarker = text.substring(idx + "Lyrics".length)
                val descEnd = afterMarker.indexOf('\n')
                return if (descEnd >= 0) afterMarker.substring(descEnd) else afterMarker
        }

        private fun appendNode(node: Node, sb: StringBuilder) {
                when (node) {
                        is TextNode -> sb.append(node.text())
                        is Element -> {
                                val name = node.normalName()
                                when {
                                        name == "br" -> sb.append('\n')
                                        isNonLyricsElement(node) -> { /* skip */
                                        }

                                        else -> node.childNodes().forEach { appendNode(it, sb) }
                                }
                        }
                }
        }

        private fun isNonLyricsElement(element: Element): Boolean {
                if (element.normalName() == "a" && element.attr("href") == "#about") return true
                val classes = element.className()
                if (classes.isBlank()) return false
                return SKIP_CLASS_PATTERNS.any { it.containsMatchIn(classes) }
        }

        private companion object {
                val SECTION_TAG_REGEX = Regex("""\[.*?]""")
                val MULTI_NEWLINE_REGEX = Regex("""\n{3,}""")
                val SKIP_CLASS_PATTERNS = listOf(
                        Regex("(?i)SongBioPreview"),
                        Regex("(?i)header"),
                        Regex("(?i)about"),
                        Regex("(?i)contributor"),
                        Regex("(?i)translation"),
                        Regex("(?i)metadata"),
                        Regex("(?i)InreadContainer"),
                        Regex("(?i)SongDescription"),
                        Regex("(?i)RightSidebar"),
                        Regex("(?i)ShareButtons"),
                        Regex("(?i)LyricsEditDesktop"),
                )
        }
}
