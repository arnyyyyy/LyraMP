package com.arno.lyramp.feature.listening_practice.domain

import kotlin.math.abs

// TODO БУДЕТ В КАРТОЧКАХ
internal object AnswerMatcher {
        fun normalize(text: String): String {
                val sb = StringBuilder(text.length)
                var prevSpace = false
                for (ch in text.lowercase()) {
                        when {
                                ch == '\'' || ch == '\u2019' || ch == '\u02BC' -> {}

                                ch.isLetterOrDigit() -> {
                                        sb.append(ch)
                                        prevSpace = false
                                }

                                ch.isWhitespace() -> if (!prevSpace) {
                                        sb.append(' ')
                                        prevSpace = true
                                }
                        }
                }
                return sb.toString().trim()
        }

        fun isAcceptable(expected: String, actual: String): Boolean {
                val normExpected = normalize(expected)
                val normActual = normalize(actual)
                return editDistance(normExpected, normActual, max = MAX_TYPOS) <= MAX_TYPOS
        }

        private fun editDistance(a: String, b: String, max: Int): Int {
                val m = a.length
                val n = b.length
                if (abs(m - n) > max) return max + 1

                val dp = Array(m + 1) { IntArray(n + 1) { max + 1 } }
                dp[0][0] = 0
                for (i in 1..m) if (i <= max) dp[i][0] = i
                for (j in 1..n) if (j <= max) dp[0][j] = j

                for (i in 1..m) {
                        val from = maxOf(1, i - max)
                        val to = minOf(n, i + max)
                        var rowMin = max + 1
                        for (j in from..to) {
                                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                                dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
                                rowMin = minOf(rowMin, dp[i][j])
                        }
                        if (rowMin > max) return max + 1
                }
                return dp[m][n]
        }

        fun typoIndicesInActual(expected: String, actual: String): Set<Int> {
                val normExpected = normalize(expected)
                val normActual = normalize(actual)
                val typosInNorm = alignmentTypoIndices(normExpected, normActual, max = MAX_TYPOS)

                val result = mutableSetOf<Int>()
                var normIdx = 0
                for (origIdx in actual.indices) {
                        val ch = actual[origIdx]
                        if (ch.isLetterOrDigit() || ch.isWhitespace()) {
                                if (normIdx in typosInNorm) result.add(origIdx)
                                normIdx++
                        }
                }
                return result
        }

        private fun alignmentTypoIndices(expected: String, actual: String, max: Int): Set<Int> {
                val m = expected.length
                val n = actual.length

                if (abs(m - n) > max) return emptySet()

                val dp = Array(m + 1) { IntArray(n + 1) { max + 1 } }

                dp[0][0] = 0
                for (i in 1..m) if (i <= max) dp[i][0] = i
                for (j in 1..n) if (j <= max) dp[0][j] = j

                for (i in 1..m) {
                        val from = maxOf(1, i - max)
                        val to = minOf(n, i + max)
                        var rowMin = max + 1
                        for (j in from..to) {
                                val cost = if (expected[i - 1] == actual[j - 1]) 0 else 1
                                dp[i][j] = minOf(
                                        dp[i - 1][j] + 1,
                                        dp[i][j - 1] + 1,
                                        dp[i - 1][j - 1] + cost
                                )

                                rowMin = minOf(rowMin, dp[i][j])
                        }
                        if (rowMin > max) return emptySet()
                }

                val typos = mutableSetOf<Int>()
                var i = m
                var j = n

                while (i > 0 || j > 0) {
                        when {
                                i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + (if (expected[i - 1] == actual[j - 1]) 0 else 1) -> {
                                        if (expected[i - 1] != actual[j - 1]) typos.add(j - 1)
                                        i--
                                        j--
                                }

                                j > 0 && dp[i][j] == dp[i][j - 1] + 1 -> {
                                        typos.add(j - 1)
                                        j--
                                }

                                else -> i--
                        }
                }
                return typos
        }

        private const val MAX_TYPOS = 3
}
