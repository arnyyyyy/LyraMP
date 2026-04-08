package com.arno.lyramp.feature.learn_words.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.arno.lyramp.feature.learn_words.resources.Res
import com.arno.lyramp.feature.learn_words.resources.counter_ratio
import com.arno.lyramp.feature.learn_words.resources.score_compact
import com.arno.lyramp.ui.theme.LyraColorScheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProgressHeader(
        currentIndex: Int,
        totalCount: Int,
        correctCount: Int,
        incorrectCount: Int,
) {
        Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = stringResource(Res.string.counter_ratio, currentIndex + 1, totalCount),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                                text = stringResource(Res.string.score_compact, correctCount, incorrectCount),
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.6f)
                        )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                ) {
                        val progress = (currentIndex + 1).toFloat() / totalCount
                        Box(
                                modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .height(3.dp)
                                        .background(Color.White, RoundedCornerShape(2.dp))
                        )
                }
        }
}

@Composable
internal fun QuestionCard(text: String) {
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                contentAlignment = Alignment.Center
        ) {
                Text(
                        text = text,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = LyraColorScheme.onSurface,
                        textAlign = TextAlign.Center
                )
        }
}

