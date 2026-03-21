package com.arno.lyramp.feature.story_generator.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.story_generator.presentation.StoryUiState
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.story_generate
import lyramp.composeapp.generated.resources.select_all
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
        val bgColor = if (isSelected) LyraColors.GlassSurface.copy(alpha = 0.6f) else LyraColors.GlassSurface
        val borderColor = if (isSelected) Color.White.copy(alpha = 0.6f) else LyraColors.GlassBorder
        val textColor = Color.White

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
                                color = textColor
                        )
                        Text(
                                text = word.translation,
                                fontSize = 11.sp,
                                color = textColor.copy(alpha = 0.7f)
                        )
                }
        }
}

@Composable
private fun SmallChipButton(text: String, onClick: () -> Unit) {
        Box(
                modifier = Modifier
                        .clickable(onClick = onClick)
                        .background(LyraColors.GlassSurface, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
                Text(text = text, fontSize = 13.sp, color = Color.White)
        }
}
