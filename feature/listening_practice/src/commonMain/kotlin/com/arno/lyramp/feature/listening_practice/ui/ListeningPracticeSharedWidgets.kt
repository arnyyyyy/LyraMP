package com.arno.lyramp.feature.listening_practice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
import com.arno.lyramp.feature.listening_practice.resources.next
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
internal fun CompactPracticeProgress(
        correctCount: Int,
        incorrectCount: Int,
        title: String? = null,
        modifier: Modifier = Modifier,
) {
        Row(
                modifier = modifier
                        .background(LyraColorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
        ) {
                ScoreBadge(
                        value = correctCount,
                        color = LyraColors.Correct,
                        label = "✓",
                        numberColor = LyraColorScheme.onSurface,
                )
                if (title != null) {
                        Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = LyraColorScheme.onSurfaceVariant,
                        )
                }
                ScoreBadge(
                        value = incorrectCount,
                        color = LyraColors.Incorrect,
                        label = "✗",
                        numberColor = LyraColorScheme.onSurface,
                )
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
        isAnswered: Boolean = false,
        onNext: () -> Unit = {},
) {
        val actionText = when {
                isAnswered -> stringResource(Res.string.next)
                userInput.isBlank() -> stringResource(Res.string.practice_skip)
                else -> stringResource(Res.string.check)
        }
        val action = when {
                isAnswered -> onNext
                userInput.isBlank() -> onSkip
                else -> onCheck
        }

        Column(modifier = modifier.fillMaxWidth()) {
                OutlinedTextField(
                        value = userInput,
                        onValueChange = { if (!isAnswered) onUserInputChange(it) },
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
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { action() }),
                        singleLine = true,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                        onClick = action,
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = if (onTransparentSurface)
                                ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = LyraColorScheme.primary,
                                )
                        else ButtonDefaults.buttonColors(containerColor = LyraColorScheme.primary),
                ) { Text(actionText, fontWeight = FontWeight.SemiBold) }
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
                        .padding(horizontal = 14.dp, vertical = 8.dp),
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

@Composable
internal fun CircleIconButton(
        label: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        size: Dp = 50.dp,
        fontSize: TextUnit = 20.sp,
) {
        Button(
                onClick = onClick,
                modifier = modifier.size(size),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = LyraColorScheme.surface,
                        contentColor = LyraColorScheme.onSurface,
                ),
        ) { Text(text = label, fontSize = fontSize) }
}

@Composable
internal fun SegmentExpandButton(
        label: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
) {
        CircleIconButton(label = label, onClick = onClick, modifier = modifier, size = 40.dp, fontSize = 18.sp)
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

