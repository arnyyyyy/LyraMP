package com.arno.lyramp.feature.listening_practice.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.listening_practice.model.LineCheckResult
import com.arno.lyramp.feature.listening_practice.model.LyricLine
import com.arno.lyramp.feature.listening_practice.model.PracticeMode
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeUiState
import com.arno.lyramp.feature.listening_practice.resources.Res
import com.arno.lyramp.feature.listening_practice.resources.full_song
import com.arno.lyramp.feature.listening_practice.resources.random_song
import com.arno.lyramp.ui.theme.LyraColorScheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ListeningPracticeReadyContent(
        state: ListeningPracticeUiState.Ready,
        onPlayPause: () -> Unit,
        onRewind: () -> Unit,
        onFastForward: () -> Unit,
        onUserInputChange: (String) -> Unit,
        onCheck: () -> Unit,
        onSkip: () -> Unit,
        onNext: () -> Unit,
        onSwitchMode: (PracticeMode) -> Unit,
        onPlayCurrentLine: () -> Unit,
        onToggleSlowMode: () -> Unit,
) {
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
        ) {
                if (state.hasTimecodes) {
                        ModeSwitcher(
                                currentMode = state.practiceMode,
                                onSwitchMode = onSwitchMode,
                                modifier = Modifier.fillMaxWidth(),
                        )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (state.practiceMode == PracticeMode.RANDOM_LINE && state.hasTimecodes) {
                        RandomLineContent(
                                state = state,
                                onPlayCurrentLine = onPlayCurrentLine,
                                onUserInputChange = onUserInputChange,
                                onCheck = onCheck,
                                onSkip = onSkip,
                                onNext = onNext,
                                onToggleSlowMode = onToggleSlowMode,
                        )
                } else {
                        FullSongContent(
                                state = state,
                                onPlayPause = onPlayPause,
                                onRewind = onRewind,
                                onFastForward = onFastForward,
                                onUserInputChange = onUserInputChange,
                                onCheck = onCheck,
                                onSkip = onSkip,
                                onToggleSlowMode = onToggleSlowMode,
                        )
                }
        }
}

@Composable
private fun FullSongContent(
        state: ListeningPracticeUiState.Ready,
        onPlayPause: () -> Unit,
        onRewind: () -> Unit,
        onFastForward: () -> Unit,
        onUserInputChange: (String) -> Unit,
        onCheck: () -> Unit,
        onSkip: () -> Unit,
        onToggleSlowMode: () -> Unit,
) {
        Column(modifier = Modifier.fillMaxSize()) {
                PlayerControls(
                        isPlaying = state.isPlaying,
                        currentPositionMs = state.currentPositionMs,
                        durationMs = state.durationMs,
                        onPlayPause = onPlayPause,
                        onRewind = onRewind,
                        onFastForward = onFastForward,
                        isSlowMode = state.isSlowMode,
                        onToggleSlowMode = onToggleSlowMode,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        PreviousLines(lines = state.lines, currentIndex = state.currentLineIndex)
                }

                Spacer(modifier = Modifier.height(12.dp))

                PracticeInputBlock(
                        userInput = state.userInput,
                        onUserInputChange = onUserInputChange,
                        onCheck = onCheck,
                        onSkip = onSkip,
                )
        }
}

@Composable
private fun RandomLineContent(
        state: ListeningPracticeUiState.Ready,
        onPlayCurrentLine: () -> Unit,
        onUserInputChange: (String) -> Unit,
        onCheck: () -> Unit,
        onSkip: () -> Unit,
        onNext: () -> Unit,
        onToggleSlowMode: () -> Unit,
) {
        Column(modifier = Modifier.fillMaxSize()) {
                LinePlayCard(
                        track = state.track,
                        isPlaying = state.currentLineIsPlaying,
                        isPlayerReady = true,
                        isSlowMode = state.isSlowMode,
                        onPlayCurrentLine = onPlayCurrentLine,
                        onToggleSlowMode = onToggleSlowMode,
                        modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(
                        visible = state.lastAnsweredLine != null,
                        enter = fadeIn(tween(250)) + slideInVertically(tween(300)) { it },
                        exit = fadeOut(tween(150)),
                ) {
                        state.lastAnsweredLine?.let {
                                Column {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        AnswerReviewCard(line = it)
                                }
                        }
                }

                Spacer(modifier = Modifier.height(8.dp))

                PracticeInputBlock(
                        userInput = state.userInput,
                        onUserInputChange = onUserInputChange,
                        onCheck = onCheck,
                        onSkip = onSkip,
                        isAnswered = state.lastAnsweredLine != null,
                        onNext = onNext,
                )
        }
}

@Composable
private fun ModeSwitcher(
        currentMode: PracticeMode,
        onSwitchMode: (PracticeMode) -> Unit,
        modifier: Modifier = Modifier,
) {
        Row(
                modifier = modifier
                        .background(LyraColorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                        .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
                ModeTab(
                        label = stringResource(Res.string.full_song),
                        isSelected = currentMode == PracticeMode.FULL_SONG,
                        onClick = { onSwitchMode(PracticeMode.FULL_SONG) },
                        modifier = Modifier.weight(1f),
                )
                ModeTab(
                        label = stringResource(Res.string.random_song),
                        isSelected = currentMode == PracticeMode.RANDOM_LINE,
                        onClick = { onSwitchMode(PracticeMode.RANDOM_LINE) },
                        modifier = Modifier.weight(1f),
                )
        }
}

@Composable
private fun ModeTab(
        label: String,
        isSelected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
) {
        Button(
                onClick = onClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) LyraColorScheme.primary else Color.Transparent,
                        contentColor = if (isSelected) LyraColorScheme.onPrimary else LyraColorScheme.onSurfaceVariant,
                ),
                modifier = modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp),
        ) {
                Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                )
        }
}

@Composable
private fun PreviousLines(lines: List<LyricLine>, currentIndex: Int) {
        if (currentIndex == 0) return
        val scrollState = rememberScrollState()
        LaunchedEffect(currentIndex) { scrollState.animateScrollTo(scrollState.maxValue) }

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
                lines.take(currentIndex).forEach { line ->
                        if (line.checkResult != LineCheckResult.PENDING) {
                                AnswerReviewCard(line = line)
                        }
                }
        }
}