package com.arno.lyramp.feature.extraction.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.extraction.domain.model.ExtractedWord
import com.arno.lyramp.feature.extraction.presentation.ExtractionUiState
import com.arno.lyramp.feature.extraction.resources.Res
import com.arno.lyramp.feature.extraction.resources.extraction_add_words
import com.arno.lyramp.feature.extraction.resources.extraction_deselect_all
import com.arno.lyramp.feature.extraction.resources.extraction_select_all
import com.arno.lyramp.feature.extraction.resources.extraction_words_count
import com.arno.lyramp.ui.LyraFilledButton
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun WordSelectionContent(
        state: ExtractionUiState.WordSelection,
        onToggleWord: (String) -> Unit,
        onToggleSelectAll: () -> Unit,
        onSave: () -> Unit
) {
        Column(modifier = Modifier.fillMaxSize()) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = if (state.selectedWords.size == state.result.words.size)
                                        stringResource(Res.string.extraction_deselect_all)
                                else
                                        stringResource(Res.string.extraction_select_all),
                                fontSize = 13.sp,
                                color = LyraColors.Correct.copy(alpha = 0.8f),
                                modifier = Modifier.clickable { onToggleSelectAll() }
                        )

                        Text(
                                text = stringResource(Res.string.extraction_words_count, state.result.words.size),
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.5f)
                        )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                        items(state.result.words, key = { it.word }) { word ->
                                ExtractedWordItem(
                                        word = word,
                                        isSelected = word.word in state.selectedWords,
                                        onToggle = { onToggleWord(word.word) }
                                )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                LyraFilledButton(
                        text = stringResource(Res.string.extraction_add_words, state.selectedWords.size),
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 35.dp),
                        containerColor = LyraColorScheme.surface,
                        contentColor = LyraColorScheme.onSurface,
                        height = 56.dp,
                        enabled = state.selectedWords.isNotEmpty()
                )
        }
}


@Composable
private fun ExtractedWordItem(
        word: ExtractedWord,
        isSelected: Boolean,
        onToggle: () -> Unit
) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onToggle)
                        .background(
                                if (isSelected) Color.White.copy(alpha = 0.08f) else Color.Transparent,
                                RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
                Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggle() },
                        colors = CheckboxDefaults.colors(
                                checkedColor = LyraColors.Correct,
                                uncheckedColor = Color.White.copy(alpha = 0.4f),
                                checkmarkColor = Color.White
                        )
                )
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = word.word,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
                        )
                        if (word.lyricLine.isNotBlank() && word.lyricLine != word.word) {
                                Text(
                                        text = word.lyricLine,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.3f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                        }
                }
                Text(
                        text = word.cefrLevel.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.35f)
                )
        }
}
