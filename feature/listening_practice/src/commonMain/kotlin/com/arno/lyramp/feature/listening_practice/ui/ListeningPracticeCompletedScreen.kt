package com.arno.lyramp.feature.listening_practice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.listening_practice.model.LineCheckResult
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeUiState
import com.arno.lyramp.feature.listening_practice.resources.Res
import com.arno.lyramp.feature.listening_practice.resources.accuracy
import com.arno.lyramp.feature.listening_practice.resources.back
import com.arno.lyramp.feature.listening_practice.resources.correct
import com.arno.lyramp.feature.listening_practice.resources.incorrect
import com.arno.lyramp.feature.listening_practice.resources.practice_completed
import com.arno.lyramp.feature.listening_practice.resources.result
import com.arno.lyramp.feature.listening_practice.resources.retry
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ListeningPracticeCompletedScreen(
        state: ListeningPracticeUiState.Completed,
        onRestart: () -> Unit,
        onBack: () -> Unit,
) {
        val scrollState = rememberScrollState()

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(24.dp),
        ) {
                Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Text(text = "🎉", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                                text = stringResource(Res.string.practice_completed),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyraColorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                StatCard(
                                        label = stringResource(Res.string.correct),
                                        value = state.correctCount,
                                        color = LyraColors.Success,
                                )
                                StatCard(
                                        label = stringResource(Res.string.incorrect),
                                        value = state.incorrectCount,
                                        color = LyraColors.Incorrect,
                                )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val total = state.correctCount + state.incorrectCount
                        val percentage = if (total > 0) (state.correctCount * 100 / total) else 0
                        Text(
                                text = stringResource(Res.string.accuracy, percentage),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyraColorScheme.primary,
                        )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                        modifier = Modifier
                                .weight(1f)
                                .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                        Text(
                                text = stringResource(Res.string.result),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyraColorScheme.onSurface,
                        )
                        state.lines.forEach { line ->
                                if (line.checkResult != LineCheckResult.PENDING) {
                                        AnswerReviewCard(line = line)
                                }
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                        OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                        ) { Text(stringResource(Res.string.back)) }
                        Button(
                                onClick = onRestart,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LyraColorScheme.primary),
                        ) { Text(stringResource(Res.string.retry), fontWeight = FontWeight.SemiBold) }
                }
        }
}

@Composable
private fun StatCard(label: String, value: Int, color: Color) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                        .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 22.dp, vertical = 12.dp),
        ) {
                Text(text = value.toString(), fontSize = 30.sp, fontWeight = FontWeight.Bold, color = color)
                Text(text = label, fontSize = 13.sp, color = LyraColorScheme.onSurfaceVariant)
        }
}
