package com.arno.lyramp.feature.stories_generator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.stories_generator.presentation.StoryUiState
import com.arno.lyramp.feature.stories_generator.resources.Res
import com.arno.lyramp.feature.stories_generator.resources.select_all
import com.arno.lyramp.feature.stories_generator.resources.story_generate
import com.arno.lyramp.ui.theme.LyraColorScheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ChooseWordsContent(
        state: StoryUiState.Ready,
        onToggleSelectAll: () -> Unit,
        onToggleWord: (Long) -> Unit,
        onGenerate: () -> Unit
) {
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                ) {
                        SmallChipButton(
                                text = stringResource(Res.string.select_all),
                                onClick = onToggleSelectAll
                        )
                }

                FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                ) {
                        state.words.forEach { word ->
                                WordChip(
                                        word = word,
                                        isSelected = word.id in state.selectedWords,
                                        onClick = { onToggleWord(word.id) }
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                StoryButton(
                        text = stringResource(Res.string.story_generate),
                        onClick = onGenerate,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.selectedWords.isNotEmpty()
                )

                Spacer(modifier = Modifier.height(16.dp))
        }
}

@Composable
private fun WordChip(
        word: LearnWordEntity,
        isSelected: Boolean,
        onClick: () -> Unit
) {
        val bgColor = if (isSelected) LyraColorScheme.primary.copy(alpha = 0.12f) else LyraColorScheme.surface
        val borderColor = if (isSelected) LyraColorScheme.primary else LyraColorScheme.outline

        Box(
                modifier = Modifier
                        .clickable(onClick = onClick)
                        .background(bgColor, RoundedCornerShape(20.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
                Column {
                        Text(
                                text = word.word,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyraColorScheme.onSurface
                        )
                        Text(
                                text = word.translation,
                                fontSize = 11.sp,
                                color = LyraColorScheme.onSurfaceVariant
                        )
                }
        }
}

@Composable
private fun SmallChipButton(text: String, onClick: () -> Unit) {
        Box(
                modifier = Modifier
                        .clickable(onClick = onClick)
                        .background(LyraColorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
                Text(text = text, fontSize = 13.sp, color = LyraColorScheme.onSurfaceVariant)
        }
}
