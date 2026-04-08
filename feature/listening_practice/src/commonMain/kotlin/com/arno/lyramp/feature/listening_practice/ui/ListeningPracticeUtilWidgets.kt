package com.arno.lyramp.feature.listening_practice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.ui.SlowModeButton
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.feature.listening_practice.formatTime

@Composable
internal fun PlayerControls(
        isPlaying: Boolean,
        currentPositionMs: Long,
        durationMs: Long,
        onPlayPause: () -> Unit,
        onRewind: () -> Unit,
        onFastForward: () -> Unit,
        isSlowMode: Boolean = false,
        onToggleSlowMode: () -> Unit = {}
) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(LyraColorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(16.dp)
        ) {
                val progress = if (durationMs > 0) currentPositionMs.toFloat() / durationMs else 0f
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(LyraColorScheme.outline, RoundedCornerShape(2.dp))
                ) {
                        Box(
                                modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .height(4.dp)
                                        .background(LyraColorScheme.primary, RoundedCornerShape(2.dp))
                        )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Text(
                                text = formatTime(currentPositionMs),
                                fontSize = 12.sp, color = LyraColorScheme.onSurfaceVariant
                        )
                        Text(
                                text = formatTime(durationMs),
                                fontSize = 12.sp, color = LyraColorScheme.onSurfaceVariant
                        )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Button(
                                onClick = onRewind,
                                modifier = Modifier.size(50.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = LyraColorScheme.surface,
                                        contentColor = LyraColorScheme.onSurface
                                )
                        ) {
                                Text(text = "⏪", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Button(
                                onClick = onPlayPause,
                                modifier = Modifier.size(60.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = LyraColorScheme.primary,
                                        contentColor = LyraColorScheme.onPrimary
                                )
                        ) {
                                Text(text = if (isPlaying) "⏸" else "▶", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Button(
                                onClick = onFastForward,
                                modifier = Modifier.size(50.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = LyraColorScheme.surface,
                                        contentColor = LyraColorScheme.onSurface
                                )
                        ) {
                                Text(text = "⏩", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        SlowModeButton(isSlowMode = isSlowMode, onClick = onToggleSlowMode)
                }
        }
}


@Composable
fun AnswerResultRow(
        isCorrect: Boolean,
        text: String,
        userInput: String? = null,
        correctLabel: String,
        incorrectLabel: String,
        correctPrefix: String,
        userWrotePrefix: String,
        modifier: Modifier = Modifier,
) {
        val bgColor = if (isCorrect) LyraColors.CorrectBg else LyraColors.IncorrectBg
        val borderColor = if (isCorrect) LyraColors.Success else LyraColors.Incorrect
        val labelColor = if (isCorrect) LyraColors.CorrectDark else LyraColors.IncorrectDark

        Column(
                modifier = modifier
                        .fillMaxWidth()
                        .background(bgColor, RoundedCornerShape(8.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                        .padding(12.dp)
        ) {
                Text(
                        text = if (isCorrect) correctLabel else incorrectLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = labelColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = correctPrefix,
                        fontSize = 13.sp,
                        color = LyraColorScheme.onSurface
                )
                if (!isCorrect && !userInput.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = userWrotePrefix,
                                fontSize = 13.sp,
                                color = LyraColorScheme.onSurfaceVariant,
                                fontStyle = FontStyle.Italic
                        )
                }
        }
}
