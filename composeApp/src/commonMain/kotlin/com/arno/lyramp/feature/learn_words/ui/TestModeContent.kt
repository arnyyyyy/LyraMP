package com.arno.lyramp.feature.learn_words.ui

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsUiState
import com.arno.lyramp.feature.learn_words.presentation.TestVariant
import com.arno.lyramp.ui.LyraFilledButton
import com.arno.lyramp.ui.theme.LyraColorScheme
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.check_icon
import lyramp.composeapp.generated.resources.cross_icon
import lyramp.composeapp.generated.resources.finish
import lyramp.composeapp.generated.resources.next
import lyramp.composeapp.generated.resources.choose_translation
import lyramp.composeapp.generated.resources.choose_word
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TestModeContent(
        state: LearnWordsUiState.Test,
        onSelectOption: (Int) -> Unit,
        onNext: () -> Unit
) {
        val questionText = when (state.variant) {
                TestVariant.FOREIGN_TO_TRANSLATION -> state.word.word
                TestVariant.TRANSLATION_TO_FOREIGN -> state.word.translation
        }

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 600.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                ProgressHeader(
                        currentIndex = state.currentIndex,
                        totalCount = state.totalCount,
                        correctCount = state.correctCount,
                        incorrectCount = state.incorrectCount
                )

                Spacer(modifier = Modifier.height(24.dp))

                QuestionCard(text = questionText)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp).align(Alignment.Start),
                        text = stringResource(
                                when (state.variant) {
                                        TestVariant.FOREIGN_TO_TRANSLATION -> Res.string.choose_translation
                                        TestVariant.TRANSLATION_TO_FOREIGN -> Res.string.choose_word
                                }
                        ),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                        state.options.forEachIndexed { index, option ->
                                TestOptionItem(
                                        option = option,
                                        index = index,
                                        state = state,
                                        onClick = { onSelectOption(index) }
                                )
                        }
                }

                if (state.isAnswered) {
                        LyraFilledButton(
                                text = if (state.currentIndex + 1 >= state.totalCount)
                                        stringResource(Res.string.finish)
                                else stringResource(Res.string.next),
                                onClick = { onNext() },
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp, bottom = 35.dp),
                                containerColor = LyraColorScheme.surface, contentColor = LyraColorScheme.onSurface, height = 65.dp,
                        )
                } else {
                        Spacer(modifier = Modifier.height(66.dp))
                }
        }
}

@Composable
private fun TestOptionItem(
        option: String,
        index: Int,
        state: LearnWordsUiState.Test,
        onClick: () -> Unit
) {
        val isAnswered = state.isAnswered
        val isSelected = state.selectedIndex == index
        val isCorrectAnswer = state.options[index] == state.correctAnswer

        val backgroundColor = when {
                !isAnswered -> LyraColorScheme.surface
                isSelected && state.isCorrect -> LyraColors.Correct
                isSelected && !state.isCorrect -> LyraColors.Incorrect
                !isSelected && isCorrectAnswer -> LyraColors.Correct.copy(alpha = 0.15f)
                else -> LyraColorScheme.surface
        }
        val textColor = when {
                !isAnswered -> LyraColors.TextPrimary
                isSelected -> Color.White
                isCorrectAnswer -> LyraColors.Correct
                else -> LyraColors.NavInactive
        }

        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isAnswered, onClick = onClick)
                        .background(backgroundColor, RoundedCornerShape(12.dp))
                        .padding(18.dp),
                contentAlignment = Alignment.CenterStart
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Text(text = option, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor, modifier = Modifier.weight(1f))
                        if (isAnswered) {
                                when {
                                        isSelected && state.isCorrect ->
                                                Text(text = stringResource(Res.string.check_icon), fontSize = 18.sp, color = Color.White)

                                        isSelected && !state.isCorrect ->
                                                Text(text = stringResource(Res.string.cross_icon), fontSize = 18.sp, color = Color.White)

                                        isCorrectAnswer ->
                                                Text(text = stringResource(Res.string.check_icon), fontSize = 18.sp, color = LyraColors.Correct)
                                }
                        }
                }
        }
}
