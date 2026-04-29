package com.arno.lyramp.feature.listening_practice.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.listening_practice.model.PracticeMode
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeScreenModel
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeUiState
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.MainFeatureScaffold
import com.arno.lyramp.feature.listening_practice.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class ListeningPracticeScreen(
        private val trackId: String,
        private val albumId: String?,
        private val trackName: String,
        private val artists: List<String>,
        private val albumName: String? = null,
        private val imageUrl: String? = null,
) : Screen {

        private val track
                get() = PracticeTrack(
                        id = trackId,
                        albumId = albumId,
                        name = trackName,
                        artists = artists,
                        albumName = albumName,
                        imageUrl = imageUrl,
                )

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val screenModel = getScreenModel<ListeningPracticeScreenModel> { parametersOf(track) }

                val uiState by screenModel.uiState.collectAsState()
                val readyState = uiState as? ListeningPracticeUiState.Ready


                MainFeatureScaffold(
                        icon = "🎧",
                        title = track.name,
                        subtitle = track.artists.joinToString(", "),
                        onBack = { navigator.pop() },
                        actions = {
                                readyState?.let { state ->
                                        val progressTitle = if (state.practiceMode == PracticeMode.RANDOM_LINE || !state.hasTimecodes) {
                                                stringResource(
                                                        Res.string.practice_line_counter,
                                                        state.currentLineIndex + 1,
                                                        state.lines.size,
                                                )
                                        } else null

                                        CompactPracticeProgress(
                                                correctCount = state.correctCount,
                                                incorrectCount = state.incorrectCount,
                                                title = progressTitle,
                                        )
                                }
                        },
                ) {
                        when (val state = uiState) {
                                is ListeningPracticeUiState.Loading -> {
                                        LoadingCard(message = stringResource(Res.string.practice_loading))
                                }

                                is ListeningPracticeUiState.Error -> {
                                        ErrorCard(message = state.message)
                                }

                                is ListeningPracticeUiState.Ready -> {
                                        ListeningPracticePlayContent(
                                                state = state,
                                                onPlayPause = screenModel::onPlayPauseClick,
                                                onRewind = screenModel::onMoveBackClick,
                                                onFastForward = screenModel::onMoveForwardClick,
                                                onUserInputChange = screenModel::onUserInputChange,
                                                onCheck = screenModel::onCheckLine,
                                                onSkip = screenModel::onSkipLine,
                                                onNext = screenModel::onNextLine,
                                                onSwitchMode = screenModel::onSwitchMode,
                                                onPlayCurrentLine = screenModel::onPlayCurrentLineClick,
                                                onToggleSlowMode = screenModel::onToggleSlowMode,
                                                onExpandLineStart = screenModel::onExpandLineStart,
                                                onExpandLineEnd = screenModel::onExpandLineEnd,
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
