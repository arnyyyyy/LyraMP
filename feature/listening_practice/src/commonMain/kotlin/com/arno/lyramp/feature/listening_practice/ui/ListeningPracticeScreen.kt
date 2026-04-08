package com.arno.lyramp.feature.listening_practice.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeScreenModel
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeUiState
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.MainFeatureScaffold
import com.arno.lyramp.feature.listening_practice.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class ListeningPracticeScreen(private val track: PracticeTrack) : Screen {

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val screenModel = getScreenModel<ListeningPracticeScreenModel> { parametersOf(track) }

                val uiState by screenModel.uiState.collectAsState()

                DisposableEffect(Unit) {
                        onDispose {
                                screenModel.onDispose()
                        }
                }

                MainFeatureScaffold(
                        icon = "🎧",
                        title = track.name,
                        subtitle = track.artists.joinToString(", "),
                        onBack = { navigator.pop() }
                ) {
                        when (val state = uiState) {
                                is ListeningPracticeUiState.Loading -> {
                                        LoadingCard(message = stringResource(Res.string.practice_loading))
                                }

                                is ListeningPracticeUiState.Error -> {
                                        ErrorCard(message = state.message)
                                }

                                is ListeningPracticeUiState.Ready -> {
                                        ReadyContent(
                                                state = state,
                                                onPlayPause = screenModel::onPlayPauseClick,
                                                onRewind = screenModel::onMoveBackClick,
                                                onFastForward = screenModel::onMoveForwardClick,
                                                onUserInputChange = screenModel::onUserInputChange,
                                                onCheck = screenModel::onCheckLine,
                                                onSkip = screenModel::onSkipLine,
                                                onSwitchMode = screenModel::onSwitchMode,
                                                onPlayCurrentLine = screenModel::onPlayCurrentLineClick,
                                                onToggleSlowMode = screenModel::onToggleSlowMode
                                        )
                                }

                                is ListeningPracticeUiState.Completed -> {
                                        ListeningPracticeCompletedScreen(
                                                state = state,
                                                onRestart = screenModel::onRestart,
                                                onBack = { navigator.pop() }
                                        )
                                }
                        }
                }
        }
}
