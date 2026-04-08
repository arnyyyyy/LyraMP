package com.arno.lyramp.feature.listening_practice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.listening_practice.resources.Res
import com.arno.lyramp.feature.listening_practice.model.LineCheckResult
import com.arno.lyramp.feature.listening_practice.model.LyricLine
import com.arno.lyramp.feature.listening_practice.model.PracticeMode
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeUiState
import com.arno.lyramp.feature.listening_practice.resources.check
import com.arno.lyramp.feature.listening_practice.resources.correct_count
import com.arno.lyramp.feature.listening_practice.resources.correct_stat
import com.arno.lyramp.feature.listening_practice.resources.correct_ticked
import com.arno.lyramp.feature.listening_practice.resources.full_song
import com.arno.lyramp.feature.listening_practice.resources.incorrect_count
import com.arno.lyramp.feature.listening_practice.resources.incorrect_ticked
import com.arno.lyramp.feature.listening_practice.resources.practice_input
import com.arno.lyramp.feature.listening_practice.resources.practice_input_placeholder
import com.arno.lyramp.feature.listening_practice.resources.practice_line_counter
import com.arno.lyramp.feature.listening_practice.resources.practice_listen_line
import com.arno.lyramp.feature.listening_practice.resources.practice_playing
import com.arno.lyramp.feature.listening_practice.resources.practice_skip
import com.arno.lyramp.feature.listening_practice.resources.random_song
import com.arno.lyramp.feature.listening_practice.resources.user_ans
import com.arno.lyramp.ui.SlowModeButton
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors

import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ReadyContent(
        state: ListeningPracticeUiState.Ready,
        onPlayPause: () -> Unit,
        onRewind: () -> Unit,
        onFastForward: () -> Unit,
        onUserInputChange: (String) -> Unit,
        onCheck: () -> Unit,
        onSkip: () -> Unit,
        onSwitchMode: (PracticeMode) -> Unit,
        onPlayCurrentLine: () -> Unit,
        onToggleSlowMode: () -> Unit
) {
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(24.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                        text = stringResource(Res.string.correct_count, state.correctCount),
                                        fontSize = 14.sp,
                                        color = LyraColors.Success,
                                        fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                        text = stringResource(Res.string.incorrect_count, state.incorrectCount),
                                        fontSize = 14.sp,
                                        color = LyraColors.Incorrect,
                                        fontWeight = FontWeight.SemiBold
                                )
                        }

                        if (state.hasTimecodes) {
                                ModeSwitcher(currentMode = state.practiceMode, onSwitchMode = onSwitchMode)
                        } else {
                                Text(
                                        text = stringResource(Res.string.practice_line_counter, state.currentLineIndex + 1, state.lines.size),
                                        fontSize = 14.sp,
                                        color = LyraColorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                )
                        }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (state.practiceMode == PracticeMode.RANDOM_LINE && state.hasTimecodes) {
                        RandomLineContent(
                                state = state,
                                onPlayCurrentLine = onPlayCurrentLine,
                                onUserInputChange = onUserInputChange,
                                onCheck = onCheck,
                                onSkip = onSkip,
                                onToggleSlowMode = onToggleSlowMode
                        )
                } else {
                        Column(modifier = Modifier.fillMaxSize().imePadding()) {
                                PlayerControls(
                                        isPlaying = state.isPlaying,
                                        currentPositionMs = state.currentPositionMs,
                                        durationMs = state.durationMs,
                                        onPlayPause = onPlayPause,
                                        onRewind = onRewind,
                                        onFastForward = onFastForward,
                                        isSlowMode = state.isSlowMode,
                                        onToggleSlowMode = onToggleSlowMode
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                        PreviousLines(lines = state.lines, currentIndex = state.currentLineIndex)
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                InputAndButtons(
                                        userInput = state.userInput,
                                        onUserInputChange = onUserInputChange,
                                        onCheck = onCheck,
                                        onSkip = onSkip
                                )
                        }
                }
        }
}

@Composable
private fun InputAndButtons(
        userInput: String,
        onUserInputChange: (String) -> Unit,
        onCheck: () -> Unit,
        onSkip: () -> Unit,
) {
        OutlinedTextField(
                value = userInput,
                onValueChange = onUserInputChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(Res.string.practice_input)) },
                placeholder = { Text(stringResource(Res.string.practice_input_placeholder)) },
                colors = TextFieldDefaults.colors(
                        focusedContainerColor = LyraColorScheme.surfaceVariant,
                        unfocusedContainerColor = LyraColorScheme.surfaceVariant,
                        focusedIndicatorColor = LyraColorScheme.primary,
                        unfocusedIndicatorColor = LyraColorScheme.outline
                ),
                shape = RoundedCornerShape(12.dp),

                keyboardOptions = KeyboardOptions(
                        imeAction = if (userInput.isNotBlank()) ImeAction.Done
                        else ImeAction.Default
                ),
                keyboardActions = KeyboardActions(
                        onDone = {
                                if (userInput.isNotBlank()) onCheck()
                                else onSkip()
                        }),
                singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                        onClick = onSkip,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LyraColorScheme.onSurfaceVariant)
                ) {
                        Text(stringResource(Res.string.practice_skip))
                }
                Button(
                        onClick = onCheck,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = LyraColorScheme.primary),
                        enabled = userInput.isNotBlank()
                ) {
                        Text(stringResource(Res.string.check))
                }
        }
}

@Composable
private fun ModeSwitcher(currentMode: PracticeMode, onSwitchMode: (PracticeMode) -> Unit) {
        Row(
                modifier = Modifier
                        .background(LyraColorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                        .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
                ModeTab(
                        label = stringResource(Res.string.full_song),
                        isSelected = currentMode == PracticeMode.FULL_SONG,
                        onClick = { onSwitchMode(PracticeMode.FULL_SONG) }
                )
                ModeTab(
                        label = stringResource(Res.string.random_song),
                        isSelected = currentMode == PracticeMode.RANDOM_LINE,
                        onClick = { onSwitchMode(PracticeMode.RANDOM_LINE) }
                )
        }
}

@Composable
private fun ModeTab(label: String, isSelected: Boolean, onClick: () -> Unit) {
        Button(
                onClick = onClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) LyraColorScheme.primary else Color.Transparent,
                        contentColor = if (isSelected) LyraColorScheme.onPrimary else LyraColorScheme.onSurfaceVariant
                ),
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
                Text(text = label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
        }
}

@Composable
private fun RandomLineContent(
        state: ListeningPracticeUiState.Ready,
        onPlayCurrentLine: () -> Unit,
        onUserInputChange: (String) -> Unit,
        onCheck: () -> Unit,
        onSkip: () -> Unit,
        onToggleSlowMode: () -> Unit
) {
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .background(LyraColors.HighlightBg, RoundedCornerShape(16.dp))
                                .border(1.dp, LyraColors.HighlightBorder, RoundedCornerShape(16.dp))
                                .padding(24.dp),
                        contentAlignment = Alignment.Center
                ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🎵", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Button(
                                                onClick = onPlayCurrentLine,
                                                shape = RoundedCornerShape(24.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (state.currentLineIsPlaying) LyraColors.Correct
                                                        else LyraColorScheme.primary,
                                                        contentColor = Color.White
                                                )
                                        ) {
                                                Text(
                                                        text = if (state.currentLineIsPlaying) stringResource(Res.string.practice_playing)
                                                        else stringResource(Res.string.practice_listen_line),
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Medium
                                                )
                                        }
                                        SlowModeButton(isSlowMode = state.isSlowMode, onClick = onToggleSlowMode)
                                }
                        }
                }

                val lastAnswered = state.lastAnsweredLine
                if (lastAnswered != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        AnswerResultRow(
                                isCorrect = lastAnswered.checkResult == LineCheckResult.CORRECT,
                                text = lastAnswered.text,
                                userInput = lastAnswered.userInput,
                                correctLabel = stringResource(Res.string.correct_ticked),
                                incorrectLabel = stringResource(Res.string.incorrect_ticked),
                                correctPrefix = stringResource(Res.string.correct_stat, lastAnswered.text),
                                userWrotePrefix = stringResource(Res.string.user_ans, lastAnswered.userInput)
                        )
                }

                Spacer(modifier = Modifier.weight(1f))

                InputAndButtons(
                        userInput = state.userInput,
                        onUserInputChange = onUserInputChange,
                        onCheck = onCheck,
                        onSkip = onSkip
                )
        }
}

@Composable
private fun PreviousLines(lines: List<LyricLine>, currentIndex: Int) {
        if (currentIndex == 0) return

        val scrollState = rememberScrollState()

        LaunchedEffect(currentIndex) {
                scrollState.animateScrollTo(scrollState.maxValue)
        }

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
        ) {
                lines.take(currentIndex).forEach { line ->
                        when (line.checkResult) {
                                LineCheckResult.CORRECT, LineCheckResult.INCORRECT -> {
                                        AnswerResultRow(
                                                isCorrect = line.checkResult == LineCheckResult.CORRECT,
                                                text = line.text,
                                                userInput = line.userInput,
                                                correctLabel = stringResource(Res.string.correct_ticked),
                                                incorrectLabel = stringResource(Res.string.incorrect_ticked),
                                                correctPrefix = stringResource(Res.string.correct_stat, line.text),
                                                userWrotePrefix = stringResource(Res.string.user_ans, line.userInput)
                                        )
                                }

                                else -> {}
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                }
        }
}