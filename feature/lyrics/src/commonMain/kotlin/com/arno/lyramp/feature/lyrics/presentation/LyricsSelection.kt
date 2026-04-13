package com.arno.lyramp.feature.lyrics.presentation

internal data class WordPosition(val lineIndex: Int, val wordIndex: Int) : Comparable<WordPosition> {
        override fun compareTo(other: WordPosition) = compareValuesBy(
                this, other, { it.lineIndex }, { it.wordIndex }
        )
}

internal data class SelectionState(
        val anchor: WordPosition? = null,
        val end: WordPosition? = null,
) {
        val isActive: Boolean
                get() = anchor != null

        fun getSelectedRange(lines: List<List<String>>): List<WordPosition> {
                val a = anchor ?: return emptyList()
                val b = end ?: return listOf(a)
                val (start, finish) = if (a <= b) a to b else b to a
                val result = mutableListOf<WordPosition>()
                for (line in start.lineIndex..finish.lineIndex) {
                        if (line < 0 || line >= lines.size) continue
                        val words = lines[line]
                        val from = if (line == start.lineIndex) start.wordIndex else 0
                        val to = if (line == finish.lineIndex) finish.wordIndex else words.lastIndex
                        for (i in from..to) result.add(WordPosition(line, i))
                }
                return result
        }
}

