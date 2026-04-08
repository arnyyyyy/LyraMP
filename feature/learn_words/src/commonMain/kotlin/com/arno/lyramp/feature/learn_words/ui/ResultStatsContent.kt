package com.arno.lyramp.feature.learn_words.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsUiState
import com.arno.lyramp.feature.learn_words.resources.Res
import com.arno.lyramp.feature.learn_words.resources.check_icon
import com.arno.lyramp.feature.learn_words.resources.correct
import com.arno.lyramp.feature.learn_words.resources.cross_icon
import com.arno.lyramp.feature.learn_words.resources.errors_num
import com.arno.lyramp.feature.learn_words.resources.words_completed_title
import com.arno.lyramp.feature.learn_words.resources.repeat
import com.arno.lyramp.ui.LyraFilledButton
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CompletedContent(state: LearnWordsUiState.Completed, onRestart: () -> Unit) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 500.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
                Text(
                        text = stringResource(Res.string.words_completed_title),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                )

                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                                .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(16.dp))
                                .padding(24.dp)
                ) {
                        Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Text(
                                                        text = stringResource(Res.string.check_icon),
                                                        fontSize = 20.sp, color = LyraColors.Correct
                                                )
                                                Text(
                                                        text = stringResource(Res.string.correct),
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = LyraColorScheme.onSurface
                                                )
                                        }
                                        Text(
                                                text = "${state.correctCount}",
                                                fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                                color = LyraColors.Correct
                                        )
                                }

                                if (state.incorrectCount > 0) {
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                        Text(
                                                                text = stringResource(Res.string.cross_icon),
                                                                fontSize = 20.sp, color = LyraColors.Incorrect
                                                        )
                                                        Text(
                                                                text = stringResource(Res.string.errors_num),
                                                                fontSize = 16.sp,
                                                                fontWeight = FontWeight.Medium,
                                                                color = LyraColorScheme.onSurface
                                                        )
                                                }
                                                Text(text = "${state.incorrectCount}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LyraColors.Incorrect)
                                        }
                                }
                        }
                }

                LyraFilledButton(
                        text = stringResource(Res.string.repeat),
                        onClick = onRestart,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = LyraColorScheme.surface, contentColor = LyraColorScheme.onSurface, height = 65.dp,
                )
        }
}
