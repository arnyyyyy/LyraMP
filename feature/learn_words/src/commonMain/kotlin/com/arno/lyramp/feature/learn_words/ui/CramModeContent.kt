package com.arno.lyramp.feature.learn_words.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.snap
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.learn_words.data.WordSource
import com.arno.lyramp.feature.learn_words.presentation.CheckResult
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsUiState
import com.arno.lyramp.ui.LyraFilledButton
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.feature.learn_words.resources.Res
import com.arno.lyramp.feature.learn_words.resources.check
import com.arno.lyramp.feature.learn_words.resources.check_icon
import com.arno.lyramp.feature.learn_words.resources.correct
import com.arno.lyramp.feature.learn_words.resources.correct_answer_is
import com.arno.lyramp.feature.learn_words.resources.cross_icon
import com.arno.lyramp.feature.learn_words.resources.finish
import com.arno.lyramp.feature.learn_words.resources.next
import com.arno.lyramp.feature.learn_words.resources.skip
import com.arno.lyramp.feature.learn_words.resources.translation
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CramModeContent(
        state: LearnWordsUiState.Cram,
        onInputChange: (String) -> Unit,
        onHintShown: () -> Unit,
        onCheck: () -> Unit,
        onSkip: () -> Unit,
        onNext: () -> Unit,
) {
        val hintSources = remember(state.word.id) {
                state.word.sources.filter { it.lyricLine.isNotBlank() }
        }
        var showHint by remember(state.word.id) { mutableStateOf(false) }
        val isAnswered = state.result != null
        val hasHint = hintSources.isNotEmpty()
        val isLast = state.currentIndex + 1 >= state.totalCount

        var feedbackState by remember {
                mutableStateOf<Pair<Boolean, String>?>(null)
        }

        LaunchedEffect(state.result) {
                if (state.result != null) {
                        feedbackState = (state.result == CheckResult.CORRECT) to (state.lastCheckedWord ?: "")
                }
        }

        LaunchedEffect(state.word.id) {
                feedbackState = null
        }

        val buttonText = when {
                !isAnswered && state.userInput.isBlank() -> stringResource(Res.string.skip)
                !isAnswered -> stringResource(Res.string.check)
                isLast -> stringResource(Res.string.finish)
                else -> stringResource(Res.string.next)
        }
        val buttonAction: () -> Unit = when {
                !isAnswered && state.userInput.isBlank() -> onSkip
                !isAnswered -> onCheck
                else -> onNext
        }

        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(state.word.id) {
                focusRequester.requestFocus()
        }

        Column(
                modifier = Modifier.fillMaxSize().widthIn(max = 600.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
                Spacer(modifier = Modifier.height(TOP_PADDING))

                Box(modifier = Modifier.fillMaxWidth()) {
                        QuestionCard(text = state.word.translation)

                        if (hasHint) {
                                Box(
                                        modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(10.dp).padding(bottom = 1.dp)
                                                .size(32.dp)
                                                .background(
                                                        if (showHint) LyraColorScheme.primary.copy(alpha = 0.20f)
                                                        else Color.White.copy(alpha = 0.15f),
                                                        CircleShape,
                                                )
                                                .clickable {
                                                        showHint = !showHint
                                                        if (showHint) onHintShown()
                                                },
                                        contentAlignment = Alignment.Center,
                                ) {
                                        Text(text = if (showHint) "💡" else "🔅", fontSize = 15.sp)
                                }
                        }
                }

                AnimatedVisibility(
                        visible = showHint && hasHint,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                ) {
                        hintSources.firstOrNull()?.let { source ->
                                CramHintCard(
                                        source = source,
                                        targetWord = state.word.word,
                                        modifier = Modifier.padding(top = 8.dp),
                                )
                        }
                }

                AnimatedVisibility(
                        visible = feedbackState != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut(snap()) + shrinkVertically(snap()),
                ) {
                        feedbackState?.let { (isCorrect, word) ->
                                CompactFeedback(
                                        isCorrect = isCorrect,
                                        correctAnswer = word,
                                        modifier = Modifier.padding(top = 8.dp),
                                )
                        }
                }

                Spacer(modifier = Modifier.weight(1f))

                OutlinedTextField(
                        value = state.userInput,
                        onValueChange = { if (!isAnswered) onInputChange(it) },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                        textStyle = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyraColors.TextPrimary,
                                textAlign = TextAlign.Center,
                        ),
                        placeholder = {
                                Text(
                                        text = stringResource(Res.string.translation),
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = LyraColors.TextPlaceholder,
                                        fontSize = 18.sp,
                                )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                                onDone = {
                                        if (isAnswered) onNext() else {
                                                if (state.userInput.isBlank()) onSkip() else onCheck()
                                        }
                                },
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LyraColorScheme.surface,
                                unfocusedContainerColor = LyraColorScheme.surface,
                                focusedBorderColor = LyraColorScheme.primary.copy(alpha = 0.4f),
                                unfocusedBorderColor = Color.Transparent,
                        ),
                )

                Spacer(modifier = Modifier.height(6.dp))

                LyraFilledButton(
                        text = buttonText,
                        onClick = buttonAction,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = LyraColorScheme.surface,
                        contentColor = LyraColorScheme.onSurface,
                        height = 56.dp,
                )

                Spacer(modifier = Modifier.height(8.dp))
        }
}

@Composable
private fun CompactFeedback(
        isCorrect: Boolean,
        correctAnswer: String,
        modifier: Modifier = Modifier,
) {
        val bgColor = if (isCorrect) LyraColors.Correct.copy(alpha = 0.15f)
        else LyraColors.Incorrect.copy(alpha = 0.12f)
        val accentColor = if (isCorrect) LyraColors.Correct else LyraColors.Incorrect

        Row(
                modifier = modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .background(bgColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
        ) {
                Text(
                        text = if (isCorrect) stringResource(Res.string.check_icon)
                        else stringResource(Res.string.cross_icon),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                )
                Spacer(modifier = Modifier.width(10.dp))
                if (isCorrect) {
                        Text(
                                text = stringResource(Res.string.correct),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accentColor,
                        )
                } else {
                        Column {
                                Text(
                                        text = stringResource(Res.string.correct_answer_is),
                                        fontSize = 12.sp,
                                        color = accentColor.copy(alpha = 0.8f),
                                )
                                Text(
                                        text = correctAnswer,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor,
                                )
                        }
                }
        }
}

@Composable
private fun CramHintCard(
        source: WordSource,
        targetWord: String,
        modifier: Modifier = Modifier,
) {
        val blankedLine = source.lyricLine.replace(targetWord, "____", ignoreCase = true)
        val primaryColor = LyraColorScheme.primary

        val annotated = buildAnnotatedString {
                val parts = blankedLine.split("____")
                parts.forEachIndexed { i, part ->
                        append(part)
                        if (i != parts.lastIndex) {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryColor)) {
                                        append("____")
                                }
                        }
                }
        }

        Column(
                modifier = modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .drawBehind {
                                drawLine(
                                        color = primaryColor,
                                        start = Offset(0f, 0f),
                                        end = Offset(0f, size.height),
                                        strokeWidth = 3.dp.toPx()
                                )
                        }
                        .padding(start = 14.dp, end = 12.dp, top = 10.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
                Text(
                        text = annotated,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                )
                Text(
                        text = "♪ ${source.trackName} — ${source.artists}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                )
        }
}
