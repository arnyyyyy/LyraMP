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
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.arno.lyramp.feature.learn_words.presentation.CheckResult
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsUiState
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.words_check_mark
import lyramp.composeapp.generated.resources.words_cross_mark
import lyramp.composeapp.generated.resources.words_finish_short
import lyramp.composeapp.generated.resources.words_learn_check
import lyramp.composeapp.generated.resources.words_learn_correct
import lyramp.composeapp.generated.resources.words_learn_incorrect
import lyramp.composeapp.generated.resources.words_learn_placeholder
import lyramp.composeapp.generated.resources.words_next
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CramModeContent(
        state: LearnWordsUiState.Cram,
        onInputChange: (String) -> Unit,
        onCheck: () -> Unit,
        onNext: () -> Unit
) {
        val result = state.result

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

                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        contentAlignment = Alignment.Center
                ) {
                        Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                        ) {
                                QuestionCard(text = state.word.translation)

                                Spacer(modifier = Modifier.height(20.dp))

                                OutlinedTextField(
                                        value = state.userInput,
                                        onValueChange = { onInputChange(it) },
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White, RoundedCornerShape(12.dp)),
                                        textStyle = TextStyle(
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1C1C1E),
                                                textAlign = TextAlign.Center
                                        ),
                                        placeholder = {
                                                Text(
                                                        text = stringResource(Res.string.words_learn_placeholder),
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textAlign = TextAlign.Center,
                                                        color = Color(0xFFC7C7CC),
                                                        fontSize = 18.sp
                                                )
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        keyboardOptions = KeyboardOptions(
                                                imeAction = if (state.userInput.isNotBlank()) ImeAction.Done else ImeAction.Default
                                        ),
                                        keyboardActions = KeyboardActions(
                                                onDone = { if (state.userInput.isNotBlank()) onCheck() }
                                        ),
                                        colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color.White,
                                                unfocusedContainerColor = Color.White,
                                                focusedIndicatorColor = Color(0xFF1C1C1E),
                                                unfocusedIndicatorColor = Color(0xFFE5E5EA)
                                        )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                when (result) {
                                        null -> Spacer(modifier = Modifier.height(20.dp))
                                        CheckResult.CORRECT -> AnswerFeedback(isCorrect = true, correctAnswer = state.word.word)
                                        CheckResult.INCORRECT -> AnswerFeedback(isCorrect = false, correctAnswer = state.word.word)
                                }
                        }
                }

                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                ) {
                        if (result == null) {
                                PrimaryButton(
                                        text = stringResource(Res.string.words_learn_check),
                                        onClick = { onCheck() },
                                        modifier = Modifier.weight(1f),
                                )
                        } else {
                                PrimaryButton(
                                        text = if (state.currentIndex + 1 >= state.totalCount) stringResource(Res.string.words_finish_short)
                                        else stringResource(Res.string.words_next),
                                        onClick = { onNext() },
                                        modifier = Modifier.weight(1f)
                                )
                        }
                }
        }
}


@Composable
private fun AnswerFeedback(
        isCorrect: Boolean,
        correctAnswer: String,
        modifier: Modifier = Modifier
) {
        val bgColor = if (isCorrect) Color(0xFF34C759).copy(alpha = 0.12f) else Color(0xFFFF3B30).copy(alpha = 0.10f)
        val accentColor = if (isCorrect) Color(0xFF34C759) else Color(0xFFFF3B30)
        val icon = if (isCorrect) stringResource(Res.string.words_check_mark) else stringResource(Res.string.words_cross_mark)

        Box(
                modifier = modifier
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
                                        text = stringResource(Res.string.words_learn_correct),
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
                                        text = stringResource(Res.string.words_learn_incorrect),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = accentColor
                                )

                                Text(
                                        text = correctAnswer,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor
                                )
                        }
                }
        }
}
