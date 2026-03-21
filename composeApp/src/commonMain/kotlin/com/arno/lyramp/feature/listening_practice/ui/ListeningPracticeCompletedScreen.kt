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
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.feature.listening_practice.model.LineCheckResult
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeUiState
import com.arno.lyramp.ui.theme.LyraColorScheme
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.accuracy
import lyramp.composeapp.generated.resources.back
import lyramp.composeapp.generated.resources.practice_completed
import lyramp.composeapp.generated.resources.correct
import lyramp.composeapp.generated.resources.correct_ticked
import lyramp.composeapp.generated.resources.correct_stat
import lyramp.composeapp.generated.resources.incorrect
import lyramp.composeapp.generated.resources.incorrect_ticked
import lyramp.composeapp.generated.resources.result
import lyramp.composeapp.generated.resources.user_ans
import lyramp.composeapp.generated.resources.retry
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ListeningPracticeCompletedScreen(
        state: ListeningPracticeUiState.Completed,
        onRestart: () -> Unit,
        onBack: () -> Unit
) {
        val scrollState = rememberScrollState()

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(24.dp)
        ) {
                Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Text(text = "🎉", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = stringResource(Res.string.practice_completed),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyraColorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                StatCard(
                                        label = stringResource(Res.string.correct),
                                        value = state.correctCount,
                                        color = LyraColors.Success
                                )
                                StatCard(
                                        label = stringResource(Res.string.incorrect),
                                        value = state.incorrectCount,
                                        color = LyraColors.Incorrect
                                )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val total = state.correctCount + state.incorrectCount
                        val percentage = if (total > 0) (state.correctCount * 100 / total) else 0
                        Text(
                                text = stringResource(Res.string.accuracy, percentage),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyraColorScheme.primary
                        )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                        modifier = Modifier
                                .weight(1f)
                                .verticalScroll(scrollState)
                ) {
                        Text(
                                text = stringResource(Res.string.result),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyraColorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 12.dp)
                        )

                        state.lines.forEach { line ->
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
                                                Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        else -> {}
                                }
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                                Text(stringResource(Res.string.back))
                        }
                        Button(
                                onClick = onRestart,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = LyraColorScheme.primary
                                )
                        ) {
                                Text(stringResource(Res.string.retry))
                        }
                }
        }
}

@Composable
private fun StatCard(label: String, value: Int, color: Color) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                        .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
                Text(text = value.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = color)
                Text(text = label, fontSize = 13.sp, color = LyraColorScheme.onSurfaceVariant)
        }
}
