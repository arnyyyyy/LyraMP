package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.koin.getScreenModel
import com.arno.lyramp.core.model.MusicTrack
import com.arno.lyramp.feature.lyrics.presentation.LyricsEvent
import com.arno.lyramp.feature.lyrics.presentation.LyricsScreenModel
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.MainFeatureScaffold
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.feature.lyrics.resources.Res
import com.arno.lyramp.feature.lyrics.resources.lyrics_loading
import com.arno.lyramp.feature.lyrics.resources.repeat
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class LyricsScreen(
        private val trackId: String?,
        private val trackName: String,
        private val artists: List<String>,
        private val albumName: String? = null,
        private val imageUrl: String? = null
) : Screen {
        private val track
                get() = MusicTrack(
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
                val selectionState by screenModel.selectionState.collectAsState()
                val highlightEnabled by screenModel.highlightEnabled.collectAsState()
                val wordLevels by screenModel.wordLevels.collectAsState()

                MainFeatureScaffold(
                        icon = "🎵",
                        title = track.name,
                        subtitle = track.artists.firstOrNull() ?: "",
                        onBack = { navigator.pop() },
                        actions = {
                                if (screenModel.canShowDifficultyButton) {
                                        DifficultyHighlightButton(
                                                isActive = highlightEnabled,
                                                onClick = { screenModel.onEvent(LyricsEvent.DifficultyHighlightToggled) }
                                        )
                                }
                        }
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
                                                selectionState = selectionState,
                                                onEvent = screenModel::onEvent,
                                                wordLevels = if (highlightEnabled) wordLevels else emptyMap(),
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun DifficultyHighlightButton(
        isActive: Boolean,
        onClick: () -> Unit,
) {
        Button(
                onClick = onClick,
                modifier = Modifier.size(36.dp).padding(end = 8.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) LyraColorScheme.primary else LyraColorScheme.surfaceVariant,
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
                Text(text = "💡", fontSize = 16.sp)
        }
}
