package com.arno.lyramp.ui

data class FlashcardItem(
        val id: Long,
        val front: String,
        val back: String,
        val subtitle: String = "",
        val progress: Float = 0f
)

data class PracticeModeItem(
        val id: String,
        val label: String,
        val icon: String
)

