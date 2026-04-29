package com.arno.lyramp.feature.learn_words.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.core.model.CefrDifficultyGroup
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsLaunchArgs
import com.arno.lyramp.feature.learn_words.presentation.LearningMode
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsScreenModel
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsUiState
import com.arno.lyramp.ui.BackButton
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.LocalNavBarHeight
import com.arno.lyramp.ui.OnboardingBackground
import com.arno.lyramp.feature.learn_words.resources.Res
import com.arno.lyramp.feature.learn_words.resources.words_loading
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

internal class LearnWordsScreen(
        private val mode: LearningMode,
        private val language: String?,
        private val cefrGroup: CefrDifficultyGroup?,
        private val useMixedPractice: Boolean = false,
        private val albumId: String? = null,
        private val trackIndex: Int? = null
) : Screen {
        @Composable
        override fun Content() {
                val screenModel = getScreenModel<LearnWordsScreenModel> {
                        parametersOf(
                                LearnWordsLaunchArgs(
                                        mode = mode,
                                        language = language,
                                        cefrGroup = cefrGroup,
                                        useMixedPractice = useMixedPractice,
                                        albumId = albumId,
                                        trackIndex = trackIndex,
                                )
                        )
                }
                val uiState by screenModel.uiState.collectAsState()
                val navigator = LocalNavigator.currentOrThrow

                val navBarHeight = LocalNavBarHeight.current
                val density = LocalDensity.current
                // Use max(navBarHeight, imeHeight) so keyboard and navBar don't double-stack
                val bottomInsets = WindowInsets.ime.union(
                        WindowInsets(0, 0, 0, with(density) { navBarHeight.roundToPx() })
                )

                Box(modifier = Modifier.fillMaxSize()) {
                        OnboardingBackground(modifier = Modifier.fillMaxSize())
                        if (useMixedPractice && uiState.isPracticeInputState()) {
                                MixedPracticeKeyboardAnchor(focusKey = uiState.keyboardAnchorKey())
                        }

                        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                                Row(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 20.dp, vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        BackButton(onClick = { navigator.pop() })

                                        val progressState = when (val s = uiState) {
                                                is LearnWordsUiState.Cram -> Triple(s.currentIndex, s.totalCount, s.correctCount to s.incorrectCount)
                                                is LearnWordsUiState.Test -> Triple(s.currentIndex, s.totalCount, s.correctCount to s.incorrectCount)
                                                else -> null
                                        }
                                        if (progressState != null) {
                                                val (currentIndex, totalCount, score) = progressState
                                                Box(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                                                        ProgressHeader(
                                                                currentIndex = currentIndex,
                                                                totalCount = totalCount,
                                                                correctCount = score.first,
                                                                incorrectCount = score.second,
                                                        )
                                                }
                                        }
                                }

                                Box(
                                        modifier = Modifier
                                                .fillMaxSize()
                                                .windowInsetsPadding(bottomInsets)
                                                .padding(horizontal = 20.dp),
                                        contentAlignment = when (uiState) {
                                                is LearnWordsUiState.Loading,
                                                is LearnWordsUiState.Completed -> Alignment.Center

                                                else -> Alignment.TopCenter
                                        }
                                ) {
                                        when (val state = uiState) {
                                                is LearnWordsUiState.Loading -> {
                                                        LoadingCard(message = stringResource(Res.string.words_loading))
                                                }

                                                is LearnWordsUiState.Cards -> {
                                                        CardsModeContent(
                                                                state = state,
                                                                onSwipe = { wordId, isKnown -> screenModel.onCardSwipe(wordId, isKnown) },
                                                                onUndo = { screenModel.undoLastSwipe() },
                                                                onToggleImportance = { wordId, isImportant -> screenModel.onToggleImportance(wordId, isImportant) },
                                                                isLoadingAudio = state.isLoadingAudio,
                                                                onPlayAudio = { wordInfo -> screenModel.playAudio(wordInfo) }
                                                        )
                                                }

                                                is LearnWordsUiState.Cram -> {
                                                        CramModeContent(
                                                                state = state,
                                                                onInputChange = { screenModel.onLearnInputChange(it) },
                                                                onHintShown = { screenModel.onLearnHintShown() },
                                                                onCheck = { screenModel.onLearnCheck() },
                                                                onSkip = { screenModel.onLearnSkip() },
                                                                onNext = { screenModel.onLearnNext() }
                                                        )
                                                }

                                                is LearnWordsUiState.Test -> {
                                                        TestModeContent(
                                                                state = state,
                                                                onSelectOption = { screenModel.onTestSelectOption(it) },
                                                                onNext = { screenModel.onTestNext() }
                                                        )
                                                }

                                                is LearnWordsUiState.Completed -> {
                                                        CompletedContent(
                                                                state = state,
                                                                onRestart = { navigator.pop() }
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun MixedPracticeKeyboardAnchor(focusKey: Long?) {
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(focusKey) {
                if (focusKey != null) {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                }
        }

        BasicTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                        .size(1.dp)
                        .alpha(0f)
                        .focusRequester(focusRequester),
        )
}

private fun LearnWordsUiState.keyboardAnchorKey(): Long? =
        (this as? LearnWordsUiState.Test)
                ?.takeIf { it.keepKeyboardVisible }
                ?.word
                ?.id

private fun LearnWordsUiState.isPracticeInputState(): Boolean =
        this is LearnWordsUiState.Cram || this is LearnWordsUiState.Test
