package com.arno.lyramp.feature.learn_words.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.core.model.CefrDifficultyGroup
import com.arno.lyramp.feature.learn_words.presentation.LearningMode
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsScreenModel
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsUiState
import com.arno.lyramp.ui.BackButton
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.OnboardingBackground
import com.arno.lyramp.feature.learn_words.resources.Res
import com.arno.lyramp.feature.learn_words.resources.words_loading
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

internal class LearnWordsScreen(
        private val mode: LearningMode,
        private val language: String?,
        private val cefrGroup: CefrDifficultyGroup?
) : Screen {
        @Composable
        override fun Content() {
                val screenModel = getScreenModel<LearnWordsScreenModel> {
                        parametersOf(mode, language, cefrGroup)
                }
                val uiState by screenModel.uiState.collectAsState()
                val navigator = LocalNavigator.currentOrThrow

                Box(modifier = Modifier.fillMaxSize()) {
                        OnboardingBackground(modifier = Modifier.fillMaxSize())

                        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
                                Row(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 20.dp, vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        BackButton(onClick = { navigator.pop() })
                                }

                                Box(
                                        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
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
                                                                onCheck = { screenModel.onLearnCheck() },
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
