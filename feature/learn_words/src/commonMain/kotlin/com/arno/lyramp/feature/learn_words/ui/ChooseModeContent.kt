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
import com.arno.lyramp.feature.learn_words.resources.Res
import com.arno.lyramp.feature.learn_words.resources.audition_card_title
import com.arno.lyramp.feature.learn_words.resources.books_icon
import com.arno.lyramp.feature.learn_words.resources.chevron
import com.arno.lyramp.feature.learn_words.resources.extraction_card_title
import com.arno.lyramp.feature.learn_words.resources.pencil_icon
import com.arno.lyramp.feature.learn_words.resources.star_icon
import com.arno.lyramp.feature.learn_words.resources.stats_card_title
import com.arno.lyramp.feature.learn_words.resources.suggestions_card_title
import com.arno.lyramp.feature.learn_words.resources.test_icon
import com.arno.lyramp.feature.learn_words.resources.words_all_words_subtitle
import com.arno.lyramp.feature.learn_words.resources.words_all_words_title
import com.arno.lyramp.feature.learn_words.resources.words_count_summary
import com.arno.lyramp.feature.learn_words.resources.words_mode_cards
import com.arno.lyramp.feature.learn_words.resources.words_mode_learn
import com.arno.lyramp.feature.learn_words.resources.words_mode_stories
import com.arno.lyramp.feature.learn_words.resources.words_mode_test
import com.arno.lyramp.feature.learn_words.resources.words_section_recommendations
import com.arno.lyramp.feature.learn_words.resources.words_section_study
import com.arno.lyramp.ui.theme.LyraColorScheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ModeSelectionContent(
        onSelectMode: (LearningMode) -> Unit,
        onNavigateToAllWords: () -> Unit,
        onNavigateToStories: () -> Unit = {},
        onNavigateToExtraction: () -> Unit = {},
        onNavigateToSuggestions: () -> Unit = {},
        onNavigateToStats: () -> Unit = {},
        onNavigateToAudition: () -> Unit = {},
        showAudition: Boolean = false,
        wordCount: Int = 0,
) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 500.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                if (wordCount > 0) {
                        WordCountPill(wordCount = wordCount)
                        Spacer(modifier = Modifier.height(4.dp))
                }

                SectionHeader(text = stringResource(Res.string.words_section_study))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        StudyModeCard(
                                icon = stringResource(Res.string.books_icon),
                                title = stringResource(Res.string.words_mode_cards),
                                modifier = Modifier.weight(1f),
                                onClick = { onSelectMode(LearningMode.CARDS) }
                        )
                        StudyModeCard(
                                icon = stringResource(Res.string.pencil_icon),
                                title = stringResource(Res.string.words_mode_learn),
                                modifier = Modifier.weight(1f),
                                onClick = { onSelectMode(LearningMode.CRAM) }
                        )
                }

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        if (showAudition) {
                                StudyModeCard(
                                        icon = "🎙️",
                                        title = stringResource(Res.string.audition_card_title),
                                        modifier = Modifier.weight(1f),
                                        onClick = onNavigateToAudition
                                )
                        } else {
                                StudyModeCard(
                                        icon = stringResource(Res.string.test_icon),
                                        title = stringResource(Res.string.words_mode_test),
                                        modifier = Modifier.weight(1f),
                                        onClick = { onSelectMode(LearningMode.TEST) }
                                )
                        }
                        StudyModeCard(
                                icon = stringResource(Res.string.star_icon),
                                title = stringResource(Res.string.words_mode_stories),
                                modifier = Modifier.weight(1f),
                                backgroundColor = Color.White.copy(alpha = 0.10f),
                                textColor = Color.White,
                                onClick = onNavigateToStories
                        )
                }

                AllWordsEntryCard(
                        title = stringResource(Res.string.words_all_words_title),
                        subtitle = stringResource(Res.string.words_all_words_subtitle),
                        count = wordCount,
                        onClick = onNavigateToAllWords
                )

                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(text = stringResource(Res.string.words_section_recommendations))

                MusicFeatureCard(
                        icon = "🎵",
                        title = stringResource(Res.string.extraction_card_title),
                        onClick = onNavigateToExtraction
                )

                MusicFeatureCard(
                        icon = "🎧",
                        title = stringResource(Res.string.suggestions_card_title),
                        onClick = onNavigateToSuggestions
                )

                MusicFeatureCard(
                        icon = "📊",
                        title = stringResource(Res.string.stats_card_title),
                        onClick = onNavigateToStats
                )

                Spacer(modifier = Modifier.height(20.dp))
        }
}

@Composable
private fun WordCountPill(wordCount: Int) {
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
                Text(
                        text = stringResource(Res.string.words_count_summary, wordCount),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                )
        }
}

@Composable
private fun SectionHeader(text: String) {
        Text(
                text = text.uppercase(),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 0.8.sp,
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, bottom = 2.dp)
        )
}

@Composable
private fun StudyModeCard(
        icon: String,
        title: String,
        modifier: Modifier = Modifier,
        backgroundColor: Color = LyraColorScheme.surface,
        textColor: Color = LyraColorScheme.onSurface,
        onClick: () -> Unit
) {
        Box(
                modifier = modifier
                        .clickable(onClick = onClick)
                        .background(backgroundColor, RoundedCornerShape(16.dp))
                        .padding(16.dp)
        ) {
                Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Text(text = icon, fontSize = 28.sp)
                        Text(
                                text = title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor
                        )
                }
        }
}

@Composable
private fun AllWordsEntryCard(
        title: String,
        subtitle: String,
        count: Int,
        onClick: () -> Unit
) {
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                        .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                        Text(text = "📋", fontSize = 26.sp)

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                )
                                Text(
                                        text = subtitle,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                )
                        }

                        if (count > 0) {
                                Text(
                                        text = count.toString(),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.6f)
                                )
                        }

                        Text(
                                text = stringResource(Res.string.chevron),
                                fontSize = 22.sp,
                                color = Color.White.copy(alpha = 0.35f),
                                fontWeight = FontWeight.Light
                        )
                }
        }
}

@Composable
internal fun MusicFeatureCard(
        icon: String,
        title: String,
        onClick: () -> Unit
) {
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                        .padding(16.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                        Text(text = icon, fontSize = 24.sp)

                        Text(
                                text = title,
                                modifier = Modifier.weight(1f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                        )

                        Text(
                                text = stringResource(Res.string.chevron),
                                fontSize = 22.sp,
                                color = Color.White.copy(alpha = 0.35f),
                                fontWeight = FontWeight.Light
                        )
                }
        }
}
