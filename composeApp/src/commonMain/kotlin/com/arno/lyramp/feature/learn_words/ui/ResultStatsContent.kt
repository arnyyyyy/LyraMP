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
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.words_check_mark
import lyramp.composeapp.generated.resources.words_completed_title
import lyramp.composeapp.generated.resources.words_cross_mark
import lyramp.composeapp.generated.resources.words_label_correct
import lyramp.composeapp.generated.resources.words_label_incorrect
import lyramp.composeapp.generated.resources.words_restart
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
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .border(1.dp, Color.Black.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
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
                                                Text(text = stringResource(Res.string.words_check_mark), fontSize = 20.sp, color = Color(0xFF34C759))
                                                Text(
                                                        text = stringResource(Res.string.words_label_correct),
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = Color(0xFF2C3E50)
                                                )
                                        }
                                        Text(
                                                text = "${state.correctCount}",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF34C759)
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
                                                        Text(text = stringResource(Res.string.words_cross_mark), fontSize = 20.sp, color = Color(0xFFFF3B30))
                                                        Text(
                                                                text = stringResource(Res.string.words_label_incorrect),
                                                                fontSize = 16.sp,
                                                                fontWeight = FontWeight.Medium,
                                                                color = Color(0xFF2C3E50)
                                                        )
                                                }
                                                Text(
                                                        text = "${state.incorrectCount}",
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFF3B30)
                                                )
                                        }
                                }
                        }
                }

                PrimaryButton(
                        text = stringResource(Res.string.words_restart),
                        onClick = onRestart,
                        modifier = Modifier.fillMaxWidth()
                )
        }
}
