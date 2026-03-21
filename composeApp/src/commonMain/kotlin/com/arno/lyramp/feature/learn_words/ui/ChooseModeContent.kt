package com.arno.lyramp.feature.learn_words.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.arno.lyramp.feature.learn_words.presentation.LearningMode
import com.arno.lyramp.ui.theme.LyraColorScheme
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.chevron
import lyramp.composeapp.generated.resources.words_mode_cards
import lyramp.composeapp.generated.resources.books_icon
import lyramp.composeapp.generated.resources.words_mode_learn
import lyramp.composeapp.generated.resources.pencil_icon
import lyramp.composeapp.generated.resources.words_mode_stories
import lyramp.composeapp.generated.resources.star_icon
import lyramp.composeapp.generated.resources.words_mode_test
import lyramp.composeapp.generated.resources.test_icon
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ModeSelectionContent(
        onSelectMode: (LearningMode) -> Unit,
        onNavigateToStories: () -> Unit = {}
) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 500.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                ModeCard(
                        icon = stringResource(Res.string.books_icon),
                        title = stringResource(Res.string.words_mode_cards),
                        onClick = { onSelectMode(LearningMode.CARDS) }
                )

                ModeCard(
                        icon = stringResource(Res.string.pencil_icon),
                        title = stringResource(Res.string.words_mode_learn),
                        onClick = { onSelectMode(LearningMode.CRAM) }
                )

                ModeCard(
                        icon = stringResource(Res.string.test_icon),
                        title = stringResource(Res.string.words_mode_test),
                        onClick = { onSelectMode(LearningMode.TEST) }
                )

                ModeCard(
                        icon = stringResource(Res.string.star_icon),
                        title = stringResource(Res.string.words_mode_stories),
                        backgroundColor = Color.White.copy(alpha = 0.15f),
                        textColor = Color.White,
                        onClick = onNavigateToStories
                )

                Spacer(modifier = Modifier.height(20.dp))
        }
}

@Composable
private fun ModeCard(
        icon: String,
        title: String,
        backgroundColor: Color = LyraColorScheme.surface,
        textColor: Color = LyraColorScheme.onSurface,
        onClick: () -> Unit
) {
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                        .background(backgroundColor, RoundedCornerShape(14.dp))
                        .padding(18.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                        Text(text = icon, fontSize = 28.sp)

                        Text(
                                text = title,
                                modifier = Modifier.weight(1f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor
                        )

                        Text(
                                text = stringResource(Res.string.chevron),
                                fontSize = 22.sp,
                                color = textColor.copy(alpha = 0.35f),
                                fontWeight = FontWeight.Light
                        )
                }
        }
}
