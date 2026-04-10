package com.arno.lyramp.core.model

fun interface WordDifficultyProvider {
        suspend fun getWordLevels(language: String): Map<String, CefrLevel>
}
