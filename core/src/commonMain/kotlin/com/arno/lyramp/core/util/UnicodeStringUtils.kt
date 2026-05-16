package com.arno.lyramp.core.util


fun String.replaceNonLetterDigitWithSpace(): String = buildString(length) {
        var prevWasSpace = true
        for (ch in this@replaceNonLetterDigitWithSpace) {
                if (ch.isLetter() || ch.isDigit()) {
                        append(ch)
                        prevWasSpace = false
                } else if (!prevWasSpace) {
                        append(' ')
                        prevWasSpace = true
                }
        }
        if (endsWith(' ')) deleteAt(lastIndex)
}

fun String.wordTokenSequence(): Sequence<String> = sequence {
        val buf = StringBuilder()
        for (ch in this@wordTokenSequence) {
                if (ch.isLetter() || ch == '\'' || ch == '-') {
                        buf.append(ch)
                } else {
                        if (buf.isNotEmpty()) {
                                yield(buf.toString())
                                buf.clear()
                        }
                }
        }
        if (buf.isNotEmpty()) yield(buf.toString())
}

