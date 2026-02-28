package com.arno.lyramp.feature.listening_practice.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.listening_practice.model.LineCheckResult
import com.arno.lyramp.feature.listening_practice.model.LyricLine
import com.arno.lyramp.util.formatTime


@Composable
internal fun PlayerControls(
        isPlaying: Boolean,
        currentPositionMs: Long,
        durationMs: Long,
        onPlayPause: () -> Unit,
        onRewind: () -> Unit,
        onFastForward: () -> Unit
) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp))
                        .padding(16.dp)
        ) {
                val progress = if (durationMs > 0) currentPositionMs.toFloat() / durationMs else 0f
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(Color(0xFFE8E8E8), RoundedCornerShape(2.dp))
                ) {
                        Box(
                                modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .height(4.dp)
                                        .background(Color(0xFF4A90E2), RoundedCornerShape(2.dp))
                        )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Text(
                                text = formatTime(currentPositionMs),
                                fontSize = 12.sp,
                                color = Color(0xFF7F8C8D)
                        )
                        Text(
                                text = formatTime(durationMs),
                                fontSize = 12.sp,
                                color = Color(0xFF7F8C8D)
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
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF2C3E50)
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
                                        containerColor = Color(0xFF4A90E2),
                                        contentColor = Color.White
                                )
                        ) {
                                Text(
                                        text = if (isPlaying) "⏸" else "▶",
                                        fontSize = 24.sp
                                )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Button(
                                onClick = onFastForward,
                                modifier = Modifier.size(50.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF2C3E50)
                                )
                        ) {
                                Text(text = "⏩", fontSize = 20.sp)
                        }
                }
        }
}

@Composable
internal fun PreviousLines(lines: List<LyricLine>, currentIndex: Int) {
        if (currentIndex == 0) return

        val scrollState = rememberScrollState()

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState, reverseScrolling = true)
        ) {
                lines.take(currentIndex).forEach { line ->
                        when (line.checkResult) {
                                LineCheckResult.CORRECT -> {
                                        ListeningPracticeLineStat(
                                                text = line.text,
                                                userInput = line.userInput,
                                                isCorrect = true
                                        )
                                }

                                LineCheckResult.INCORRECT -> {
                                        ListeningPracticeLineStat(
                                                text = line.text,
                                                userInput = line.userInput,
                                                isCorrect = false
                                        )
                                }

                                else -> {}
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                }
        }
}