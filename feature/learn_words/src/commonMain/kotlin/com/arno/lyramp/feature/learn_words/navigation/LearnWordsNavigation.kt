package com.arno.lyramp.feature.learn_words.navigation

import cafe.adriel.voyager.core.screen.Screen
import com.arno.lyramp.feature.learn_words.presentation.LearningMode
import com.arno.lyramp.feature.learn_words.ui.LearnWordsScreen

enum class LearnWordsModeArg { CARDS, CRAM, TEST }

fun learnWordsScreenForTrack(
        mode: LearnWordsModeArg,
        albumId: String,
        trackIndex: Int
): Screen = LearnWordsScreen(
        mode = when (mode) {
                LearnWordsModeArg.CARDS -> LearningMode.CARDS
                LearnWordsModeArg.CRAM -> LearningMode.CRAM
                LearnWordsModeArg.TEST -> LearningMode.TEST
        },
        language = null,
        cefrGroup = null,
        albumId = albumId,
        trackIndex = trackIndex
)
