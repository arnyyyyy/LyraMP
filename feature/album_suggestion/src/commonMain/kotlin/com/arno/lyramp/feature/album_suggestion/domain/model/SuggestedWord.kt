package com.arno.lyramp.feature.album_suggestion.domain.model

import com.arno.lyramp.core.model.CefrLevel

internal data class SuggestedWord(
        val word: String,
        val cefrLevel: CefrLevel? = null,
        val lyricLine: String = "",
        val trackName: String = "",
        val artists: String = "",
        val trackIndex: Int = 0
)

internal val SuggestedWordComparator: Comparator<SuggestedWord> =
        compareBy<SuggestedWord> { it.cefrLevel?.ordinal ?: Int.MAX_VALUE }.thenBy { it.word }
