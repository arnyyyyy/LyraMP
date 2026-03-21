package com.arno.lyramp.feature.learn_words.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.feature.learn_words.presentation.CheckResult
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsUiState
import com.arno.lyramp.ui.LyraFilledButton
import com.arno.lyramp.ui.theme.LyraColorScheme
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.check_icon
import lyramp.composeapp.generated.resources.cross_icon
import lyramp.composeapp.generated.resources.finish
import lyramp.composeapp.generated.resources.check
import lyramp.composeapp.generated.resources.correct
import lyramp.composeapp.generated.resources.correct_answer_is
import lyramp.composeapp.generated.resources.translation
import lyramp.composeapp.generated.resources.next
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CramModeContent(
        state: LearnWordsUiState.Cram,
        onInputChange: (String) -> Unit,
        onCheck: () -> Unit,
        onNext: () -> Unit
) {
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 600.dp)
                        .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                ProgressHeader(
                        currentIndex = state.currentIndex,
                        totalCount = state.totalCount,
                        correctCount = state.correctCount,
                        incorrectCount = state.incorrectCount
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                        ) {
                                QuestionCard(text = state.word.translation)

                                Spacer(modifier = Modifier.height(20.dp))


                                Box(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                                                .padding(20.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        OutlinedTextField(
                                                value = state.userInput,
                                                onValueChange = { onInputChange(it) },
                                                modifier = Modifier.fillMaxWidth(),
                                                textStyle = TextStyle(
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = LyraColors.TextPrimary,
                                                        textAlign = TextAlign.Center
                                                ),
                                                placeholder = {
                                                        Text(
                                                                text = stringResource(Res.string.translation),
                                                                modifier = Modifier.fillMaxWidth(),
                                                                textAlign = TextAlign.Center,
                                                                color = LyraColors.TextPlaceholder,
                                                                fontSize = 18.sp
                                                        )
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                keyboardOptions = KeyboardOptions(
                                                        imeAction = if (state.userInput.isNotBlank()) ImeAction.Done else ImeAction.Default
                                                ),
                                                keyboardActions = KeyboardActions(
                                                        onDone = { if (state.userInput.isNotBlank()) onCheck() }
                                                ),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                        focusedContainerColor = LyraColorScheme.surface,
                                                        unfocusedContainerColor = LyraColorScheme.surface,
                                                        focusedBorderColor = Color.Transparent,
                                                        unfocusedBorderColor = Color.Transparent
                                                )
                                        )
                                }


                                Spacer(modifier = Modifier.height(12.dp))

                                when (state.result) {
                                        null -> Spacer(modifier = Modifier.height(20.dp))
                                        CheckResult.CORRECT -> AnswerFeedback(isCorrect = true, correctAnswer = state.word.word)
                                        CheckResult.INCORRECT -> AnswerFeedback(isCorrect = false, correctAnswer = state.word.word)
                                }
                        }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        if (state.result == null) {
                                LyraFilledButton(
                                        text = stringResource(Res.string.check),
                                        onClick = { onCheck() },
                                        modifier = Modifier.weight(1f),
                                        containerColor = LyraColorScheme.surface,
                                        contentColor = LyraColorScheme.onSurface,
                                        height = 65.dp,
                                )
                        } else {
                                LyraFilledButton(
                                        text = if (state.currentIndex + 1 >= state.totalCount) stringResource(Res.string.finish)
                                        else stringResource(Res.string.next),
                                        onClick = { onNext() },
                                        modifier = Modifier.weight(1f),
                                        containerColor = LyraColorScheme.surface,
                                        contentColor = LyraColorScheme.onSurface,
                                        height = 65.dp,
                                )
                        }
                }
        }
}

@Composable
private fun AnswerFeedback(isCorrect: Boolean, correctAnswer: String) {
        val bgColor = if (isCorrect) LyraColors.Correct.copy(alpha = 0.12f) else LyraColors.Incorrect.copy(alpha = 0.10f)
        val accentColor = if (isCorrect) LyraColors.Correct else LyraColors.Incorrect
        val icon = if (isCorrect) stringResource(Res.string.check_icon) else stringResource(Res.string.cross_icon)

        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor, RoundedCornerShape(10.dp))
                        .padding(14.dp),
                contentAlignment = Alignment.Center
        ) {
                if (isCorrect) {
                        Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(text = icon, fontSize = 18.sp, color = accentColor)
                                Text(
                                        text = stringResource(Res.string.correct),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = accentColor
                                )
                        }
                } else {
                        Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(text = icon, fontSize = 18.sp, color = accentColor)
                                Text(
                                        text = stringResource(Res.string.correct_answer_is),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = accentColor
                                )
                                Text(text = correctAnswer, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = accentColor)
                        }
                }
        }
}
