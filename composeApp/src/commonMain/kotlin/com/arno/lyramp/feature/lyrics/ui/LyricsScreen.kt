package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.koin.getScreenModel
import com.arno.lyramp.ui.MainFeatureScaffold
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.feature.lyrics.presentation.LyricsScreenModel
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.repeat
import lyramp.composeapp.generated.resources.lyrics_loading
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

internal class LyricsScreen(
        private val trackId: String?,
        private val trackName: String,
        private val artists: List<String>,
        private val albumName: String? = null,
        private val imageUrl: String? = null
) : Screen {
        private val track
                get() = ListeningHistoryMusicTrack(
                        id = trackId,
                        name = trackName,
                        artists = artists,
                        albumName = albumName,
                        imageUrl = imageUrl
                )

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow

                val screenModel = getScreenModel<LyricsScreenModel> { parametersOf(track) }

                val uiState by screenModel.uiState.collectAsState()
                val popupState by screenModel.popupState.collectAsState()

                MainFeatureScaffold(
                        icon = "🎵",
                        title = track.name,
                        subtitle = track.artists.firstOrNull() ?: "",
                        onBack = { navigator.pop() }
                ) {
                        when (val state = uiState) {
                                is LyricsUiState.Loading -> {
                                        LoadingCard(message = stringResource(Res.string.lyrics_loading))
                                }

                                is LyricsUiState.Error -> {
                                        ErrorCard(
                                                message = state.message,
                                                onRetry = screenModel::loadLyrics,
                                                retryLabel = stringResource(Res.string.repeat)
                                        )
                                }

                                is LyricsUiState.Success -> {
                                        LyricsSuccessCard(
                                                lyricsLines = state.lyricsLines,
                                                popupState = popupState,
                                                onEvent = screenModel::onEvent,
                                        )
                                }
                        }
                }
        }
}
