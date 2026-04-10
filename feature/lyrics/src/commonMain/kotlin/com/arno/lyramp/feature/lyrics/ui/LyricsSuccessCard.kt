package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.feature.lyrics.presentation.LyricsEvent
import com.arno.lyramp.feature.lyrics.presentation.SelectionState
import com.arno.lyramp.feature.lyrics.presentation.WordPopupState
import com.arno.lyramp.feature.lyrics.presentation.WordPosition
import com.arno.lyramp.ui.theme.LyraColorScheme

@Composable
internal fun LyricsSuccessCard(
        lyricsLines: List<List<String>>,
        popupState: WordPopupState,
        selectionState: SelectionState,
        onEvent: (LyricsEvent) -> Unit,
        wordLevels: Map<String, CefrLevel> = emptyMap(),
) {
        val scrollState = rememberScrollState()
        val selectedPositions = remember(selectionState, lyricsLines) {
                selectionState.getSelectedRange(lyricsLines).toSet()
        }
        val popupAnchor = (popupState as? WordPopupState.Visible)?.anchorPosition

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(24.dp)
        ) {
                Column(
                        modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 8.dp)
                ) {
                        lyricsLines.forEachIndexed { lineIndex, words ->
                                if (words.isEmpty()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                } else {
                                        FlowRow(
                                                horizontalArrangement = Arrangement.Start,
                                                verticalArrangement = Arrangement.Top,
                                                modifier = Modifier.fillMaxWidth(),
                                                maxItemsInEachRow = Int.MAX_VALUE
                                        ) {
                                                words.forEachIndexed { wordIndex, word ->
                                                        val pos = WordPosition(lineIndex, wordIndex)
                                                        val isSelected = pos in selectedPositions
                                                        val isAnchor = pos == popupAnchor

                                                        Box {
                                                                LyricsWord(
                                                                        word = word,
                                                                        isSelected = isSelected,
                                                                        isHighlighted = isAnchor,
                                                                        cefrLevel = wordLevels[word.lowercase().trim { !it.isLetter() }],
                                                                        onClick = {
                                                                                if (selectionState.isActive) {
                                                                                        onEvent(LyricsEvent.SelectionExtended(lineIndex, wordIndex))
                                                                                } else if (isAnchor) {
                                                                                        onEvent(LyricsEvent.PopupDismissed)
                                                                                } else {
                                                                                        onEvent(LyricsEvent.WordTapped(lineIndex, wordIndex))
                                                                                }
                                                                        },
                                                                        onLongClick = {
                                                                                onEvent(LyricsEvent.SelectionStarted(lineIndex, wordIndex))
                                                                        }
                                                                )
                                                                if (isAnchor) TranslationPopup(state = popupState, onEvent = onEvent)
                                                        }
                                                }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                }
        }
}
