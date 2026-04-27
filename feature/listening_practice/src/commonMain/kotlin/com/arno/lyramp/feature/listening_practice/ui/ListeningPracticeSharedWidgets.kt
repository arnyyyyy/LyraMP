package com.arno.lyramp.feature.listening_practice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.listening_practice.domain.AnswerMatcher
import com.arno.lyramp.feature.listening_practice.model.LineCheckResult
import com.arno.lyramp.feature.listening_practice.model.LyricLine
import com.arno.lyramp.feature.listening_practice.resources.Res
import com.arno.lyramp.feature.listening_practice.resources.check
import com.arno.lyramp.feature.listening_practice.resources.correct_stat
import com.arno.lyramp.feature.listening_practice.resources.correct_ticked
import com.arno.lyramp.feature.listening_practice.resources.incorrect_ticked
import com.arno.lyramp.feature.listening_practice.resources.practice_input
import com.arno.lyramp.feature.listening_practice.resources.practice_input_placeholder
import com.arno.lyramp.feature.listening_practice.resources.practice_skip
import com.arno.lyramp.feature.listening_practice.resources.user_ans
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PracticeScoreHeader(
        correctCount: Int,
        incorrectCount: Int,
        modifier: Modifier = Modifier,
        title: String? = null,
        trailing: (@Composable () -> Unit)? = null,
) {
        val bg = LyraColorScheme.surfaceVariant
        val titleColor = LyraColorScheme.onSurfaceVariant
        val numberColor = LyraColorScheme.onSurface

        Row(
                modifier = modifier
                        .fillMaxWidth()
                        .background(bg, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
        ) {
                ScoreBadge(value = correctCount, color = LyraColors.Correct, label = "✓", numberColor = numberColor)
                if (title != null) {
                        Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = titleColor,
                                letterSpacing = 1.4.sp,
                        )
                } else if (trailing != null) {
                        trailing()
                } else {
                        Spacer(modifier = Modifier.size(0.dp))
                }
                ScoreBadge(value = incorrectCount, color = LyraColors.Incorrect, label = "✗", numberColor = numberColor)
        }
}

@Composable
private fun ScoreBadge(value: Int, color: Color, label: String, numberColor: Color) {
        Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
                Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
                Text(text = value.toString(), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = numberColor)
        }
}

@Composable
internal fun PracticeInputBlock(
        userInput: String,
        onUserInputChange: (String) -> Unit,
        onCheck: () -> Unit,
        onSkip: () -> Unit,
        modifier: Modifier = Modifier,
        onTransparentSurface: Boolean = false,
) {
        Column(modifier = modifier.fillMaxWidth()) {
                OutlinedTextField(
                        value = userInput,
                        onValueChange = onUserInputChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(Res.string.practice_input)) },
                        placeholder = {
                                Text(
                                        stringResource(Res.string.practice_input_placeholder),
                                        color = if (onTransparentSurface) LyraColors.TextPlaceholder
                                        else LyraColorScheme.onSurfaceVariant,
                                )
                        },
                        colors = if (onTransparentSurface) {
                                TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.92f),
                                        focusedIndicatorColor = LyraColorScheme.primary,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = LyraColors.TextPrimary,
                                        unfocusedTextColor = LyraColors.TextPrimary,
                                        focusedLabelColor = LyraColorScheme.primary,
                                        unfocusedLabelColor = LyraColors.TextSubtle,
                                )
                        } else {
                                TextFieldDefaults.colors(
                                        focusedContainerColor = LyraColorScheme.surfaceVariant,
                                        unfocusedContainerColor = LyraColorScheme.surfaceVariant,
                                        focusedIndicatorColor = LyraColorScheme.primary,
                                        unfocusedIndicatorColor = LyraColorScheme.outline,
                                )
                        },
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                                imeAction = if (userInput.isNotBlank()) ImeAction.Done else ImeAction.Default,
                        ),
                        keyboardActions = KeyboardActions(
                                onDone = { if (userInput.isNotBlank()) onCheck() else onSkip() },
                        ),
                        singleLine = true,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                        OutlinedButton(
                                onClick = onSkip,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = if (onTransparentSurface)
                                        ButtonDefaults.outlinedButtonColors(contentColor = Color.White.copy(alpha = 0.85f))
                                else ButtonDefaults.outlinedButtonColors(contentColor = LyraColorScheme.onSurfaceVariant),
                        ) { Text(stringResource(Res.string.practice_skip), fontWeight = FontWeight.Medium) }

                        Button(
                                onClick = onCheck,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = if (onTransparentSurface)
                                        ButtonDefaults.buttonColors(
                                                containerColor = Color.White,
                                                contentColor = LyraColorScheme.primary,
                                                disabledContainerColor = Color.White.copy(alpha = 0.4f),
                                                disabledContentColor = LyraColors.TextPlaceholder,
                                        )
                                else ButtonDefaults.buttonColors(containerColor = LyraColorScheme.primary),
                                enabled = userInput.isNotBlank(),
                        ) { Text(stringResource(Res.string.check), fontWeight = FontWeight.SemiBold) }
                }
        }
}

@Composable
internal fun AnswerReviewCard(
        line: LyricLine,
        modifier: Modifier = Modifier,
) {
        val isCorrect = line.checkResult == LineCheckResult.CORRECT
        val bg = if (isCorrect) LyraColors.CorrectBg else LyraColors.IncorrectBg
        val border = if (isCorrect) LyraColors.Success else LyraColors.Incorrect
        val labelColor = if (isCorrect) LyraColors.CorrectDark else LyraColors.IncorrectDark

        Column(
                modifier = modifier
                        .fillMaxWidth()
                        .background(bg, RoundedCornerShape(12.dp))
                        .border(1.dp, border.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
                Text(
                        text = if (isCorrect) stringResource(Res.string.correct_ticked)
                        else stringResource(Res.string.incorrect_ticked),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = labelColor,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = stringResource(Res.string.correct_stat, line.text),
                        fontSize = 13.sp,
                        color = LyraColors.TextPrimary,
                )
                if (line.userInput.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        val prefix = stringResource(Res.string.user_ans, "")
                        val highlighted = buildTypoAnnotatedString(
                                expected = line.text,
                                actual = line.userInput,
                                isCorrect = isCorrect,
                        )
                        Text(
                                text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = LyraColors.TextSubtle)) { append(prefix) }
                                        append(highlighted)
                                },
                                fontSize = 12.sp,
                        )
                }
        }
}

internal fun buildTypoAnnotatedString(
        expected: String,
        actual: String,
        isCorrect: Boolean,
): AnnotatedString {
        val typos = if (!isCorrect) AnswerMatcher.typoIndicesInActual(expected, actual) else emptySet()
        return buildAnnotatedString {
                actual.forEachIndexed { idx, ch ->
                        val isPunct = !ch.isLetterOrDigit() && !ch.isWhitespace()
                        when {
                                idx in typos -> withStyle(
                                        SpanStyle(
                                                color = LyraColors.Incorrect,
                                                fontWeight = FontWeight.SemiBold,
                                                textDecoration = TextDecoration.Underline,
                                        ),
                                ) { append(ch) }

                                isPunct -> withStyle(SpanStyle(color = LyraColors.TextSubtle)) { append(ch) }
                                else -> withStyle(SpanStyle(color = LyraColors.TextPrimary)) { append(ch) }
                        }
                }
        }
}

