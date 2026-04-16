package com.arno.lyramp.feature.extraction.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.arno.lyramp.feature.extraction.presentation.ExtractionUiState
import com.arno.lyramp.feature.extraction.resources.Res
import com.arno.lyramp.feature.extraction.resources.extraction_add_words
import com.arno.lyramp.feature.extraction.resources.extraction_deselect_all
import com.arno.lyramp.feature.extraction.resources.extraction_select_all
import com.arno.lyramp.feature.extraction.resources.extraction_words_count
import com.arno.lyramp.ui.WordItem
import com.arno.lyramp.ui.WordSelectionList
import com.arno.lyramp.ui.theme.LyraColors
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun WordSelectionContent(
        state: ExtractionUiState.WordSelection,
        onToggleWord: (String) -> Unit,
        onToggleSelectAll: () -> Unit,
        onSave: () -> Unit
) {
        WordSelectionList(
                words = state.result.words.map { w ->
                        WordItem(
                                word = w.word,
                                subtitle = w.lyricLine,
                                levelTag = w.cefrLevel.name
                        )
                },
                selectedWords = state.selectedWords,
                onToggleWord = onToggleWord,
                onSave = onSave,
                saveButtonText = stringResource(Res.string.extraction_add_words, state.selectedWords.size),
                saveEnabled = state.selectedWords.isNotEmpty(),
                headerContent = {
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
                },
                modifier = Modifier.fillMaxSize()
        )
}
