package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.koin.getScreenModel
import com.arno.lyramp.core.model.MusicTrack
import com.arno.lyramp.feature.lyrics.presentation.LyricsEvent
import com.arno.lyramp.feature.lyrics.presentation.LyricsScreenModel
import com.arno.lyramp.feature.lyrics.presentation.LyricsUiState
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.MainFeatureScaffold
import com.arno.lyramp.ui.ToolbarActionButton
import com.arno.lyramp.feature.lyrics.resources.Res
import com.arno.lyramp.feature.lyrics.resources.enter_text
import com.arno.lyramp.feature.lyrics.resources.lyrics_loading
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
                                if (uiState is LyricsUiState.Success) {
                                        LyricsActionsRow(
                                                onEditClick = { screenModel.onEvent(LyricsEvent.EditLyrics) },
                                                showHighlight = screenModel.canShowDifficultyButton,
                                                highlightEnabled = highlightEnabled,
                                                onHighlightClick = { screenModel.onEvent(LyricsEvent.DifficultyHighlightToggled) },
                                        )
                                } else if (screenModel.canShowDifficultyButton) {
                                        ToolbarActionButton(
                                                emoji = "💡",
                                                onClick = { screenModel.onEvent(LyricsEvent.DifficultyHighlightToggled) },
                                                isActive = highlightEnabled,
                                                modifier = Modifier.padding(end = 8.dp),
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
                                                onRetry = { screenModel.onEvent(LyricsEvent.AddLyrics) },
                                                retryLabel = stringResource(Res.string.enter_text)
                                        )
                                }

                                is LyricsUiState.Editing -> {
                                        LyricsTextEditor(
                                                initialText = state.initialText,
                                                onSubmit = { screenModel.onEvent(LyricsEvent.UpdateLyrics(it)) }
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
private fun LyricsActionsRow(
        onEditClick: () -> Unit,
        showHighlight: Boolean,
        highlightEnabled: Boolean,
        onHighlightClick: () -> Unit,
) {
        var editRevealed by remember { mutableStateOf(!showHighlight) }

        Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = if (showHighlight) {
                        Modifier.pointerInput(Unit) {
                                var totalDrag = 0f
                                detectHorizontalDragGestures(
                                        onDragStart = { totalDrag = 0f },
                                        onDragEnd = {
                                                if (totalDrag < -20) editRevealed = true
                                                else if (totalDrag > 20) editRevealed = false
                                        },
                                        onHorizontalDrag = { _, dragAmount ->
                                                totalDrag += dragAmount
                                        }
                                )
                        }
                } else Modifier
        ) {
                AnimatedVisibility(
                        visible = editRevealed,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                ) {
                        ToolbarActionButton(
                                emoji = "✏️",
                                onClick = onEditClick,
                                modifier = Modifier.padding(end = 8.dp),
                        )
                }
                if (showHighlight) {
                        ToolbarActionButton(
                                emoji = "💡",
                                onClick = onHighlightClick,
                                isActive = highlightEnabled,
                                modifier = Modifier.padding(end = 8.dp),
                        )
                }
        }
}
