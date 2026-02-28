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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.listening_practice.model.LineCheckResult
import com.arno.lyramp.feature.listening_practice.model.PracticeMode
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeUiState
import lyramp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LoadingContent() {
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(16.dp))
                        .padding(40.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF4A90E2)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                                text = stringResource(Res.string.practice_loading),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2C3E50)
                        )
                }
        }
}

@Composable
internal fun ErrorContent(message: String) {
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(16.dp))
                        .padding(32.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "❌", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = message,
                                fontSize = 16.sp,
                                color = Color(0xFFD32F2F),
                                textAlign = TextAlign.Center
                        )
                }
        }
}

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
        onPlayCurrentLine: () -> Unit
) {
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(16.dp))
                        .padding(24.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                        text = "✓ ${state.correctCount}",
                                        fontSize = 14.sp,
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                        text = "✗ ${state.incorrectCount}",
                                        fontSize = 14.sp,
                                        color = Color(0xFFF44336),
                                        fontWeight = FontWeight.SemiBold
                                )
                        }

                        if (state.hasTimecodes) {
                                ModeSwitcher(
                                        currentMode = state.practiceMode,
                                        onSwitchMode = onSwitchMode
                                )
                        } else {
                                Text(
                                        text = stringResource(Res.string.practice_line_counter, state.currentLineIndex + 1, state.lines.size),
                                        fontSize = 14.sp,
                                        color = Color(0xFF7F8C8D),
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
                                onSkip = onSkip
                        )
                } else {
                        PlayerControls(
                                isPlaying = state.isPlaying,
                                currentPositionMs = state.currentPositionMs,
                                durationMs = state.durationMs,
                                onPlayPause = onPlayPause,
                                onRewind = onRewind,
                                onFastForward = onFastForward
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                        ) {
                                PreviousLines(
                                        lines = state.lines,
                                        currentIndex = state.currentLineIndex
                                )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                                value = state.userInput,
                                onValueChange = onUserInputChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(stringResource(Res.string.practice_input_label)) },
                                placeholder = { Text(stringResource(Res.string.practice_input_placeholder)) },
                                colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFF9F9F9),
                                        unfocusedContainerColor = Color(0xFFF9F9F9),
                                        focusedIndicatorColor = Color(0xFF4A90E2),
                                        unfocusedIndicatorColor = Color(0xFFE8E8E8)
                                ),
                                shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                OutlinedButton(
                                        onClick = onSkip,
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color(0xFF7F8C8D)
                                        )
                                ) {
                                        Text(stringResource(Res.string.practice_skip))
                                }
                                Button(
                                        onClick = onCheck,
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF4A90E2)
                                        ),
                                        enabled = state.userInput.isNotBlank()
                                ) {
                                        Text(stringResource(Res.string.practice_check))
                                }
                        }
                }
        }
}

@Composable
private fun ModeSwitcher(
        currentMode: PracticeMode,
        onSwitchMode: (PracticeMode) -> Unit
) {
        Row(
                modifier = Modifier
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(20.dp))
                        .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
                ModeTab(
                        label = stringResource(Res.string.practice_mode_full_song),
                        isSelected = currentMode == PracticeMode.FULL_SONG,
                        onClick = { onSwitchMode(PracticeMode.FULL_SONG) }
                )
                ModeTab(
                        label = stringResource(Res.string.practice_mode_random),
                        isSelected = currentMode == PracticeMode.RANDOM_LINE,
                        onClick = { onSwitchMode(PracticeMode.RANDOM_LINE) }
                )
        }
}

@Composable
private fun ModeTab(
        label: String,
        isSelected: Boolean,
        onClick: () -> Unit
) {
        Button(
                onClick = onClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF4A90E2) else Color.Transparent,
                        contentColor = if (isSelected) Color.White else Color(0xFF7F8C8D)
                ),
                modifier = Modifier.height(32.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
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
        onSkip: () -> Unit
) {
        Column(modifier = Modifier.fillMaxSize()) {
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF0F5FF), RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFD0E4FF), RoundedCornerShape(16.dp))
                                .padding(24.dp),
                        contentAlignment = Alignment.Center
                ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                        text = "🎵",
                                        fontSize = 40.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                        text = stringResource(Res.string.practice_line_hint),
                                        fontSize = 14.sp,
                                        color = Color(0xFF5A7A9D),
                                        textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                        onClick = onPlayCurrentLine,
                                        shape = RoundedCornerShape(24.dp),
                                        colors = ButtonDefaults.buttonColors(
                                                containerColor = if (state.currentLineIsPlaying) Color(0xFF34C759) else Color(0xFF4A90E2),
                                                contentColor = Color.White
                                        )
                                ) {
                                        Text(
                                                text = if (state.currentLineIsPlaying) stringResource(Res.string.practice_playing) else stringResource(Res.string.practice_listen_line),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium
                                        )
                                }
                        }
                }

                Spacer(modifier = Modifier.weight(1f))

                val answeredLines = state.lines.filter {
                        it.checkResult != LineCheckResult.PENDING
                }
                if (answeredLines.isNotEmpty()) {
                        Text(
                                text = stringResource(Res.string.practice_previous_count, answeredLines.size),
                                fontSize = 12.sp,
                                color = Color(0xFF7F8C8D)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                        value = state.userInput,
                        onValueChange = onUserInputChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(Res.string.practice_input_label)) },
                        placeholder = { Text(stringResource(Res.string.practice_input_placeholder)) },
                        colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF9F9F9),
                                unfocusedContainerColor = Color(0xFFF9F9F9),
                                focusedIndicatorColor = Color(0xFF4A90E2),
                                unfocusedIndicatorColor = Color(0xFFE8E8E8)
                        ),
                        shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        OutlinedButton(
                                onClick = onSkip,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF7F8C8D)
                                )
                        ) {
                                Text(stringResource(Res.string.practice_skip))
                        }
                        Button(
                                onClick = onCheck,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4A90E2)
                                ),
                                enabled = state.userInput.isNotBlank()
                        ) {
                                Text(stringResource(Res.string.practice_check))
                        }
                }
        }
}