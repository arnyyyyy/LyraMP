package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arno.lyramp.ui.theme.LyraColorScheme

@Composable
internal fun LyricsSuccessCard(
        lyricsLines: List<List<String>>,
        popupState: WordPopupState,
        onEvent: (LyricsEvent) -> Unit,
) {
        val scrollState = rememberScrollState()

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
                                        val lyricLine = words.joinToString(" ")

                                        FlowRow(
                                                horizontalArrangement = Arrangement.Start,
                                                verticalArrangement = Arrangement.Top,
                                                modifier = Modifier.fillMaxWidth(),
                                                maxItemsInEachRow = Int.MAX_VALUE
                                        ) {
                                                words.forEachIndexed { wordIndex, word ->
                                                        LyricsWord(
                                                                word = word,
                                                                lyricLine = lyricLine,
                                                                lineIndex = lineIndex,
                                                                wordIndex = wordIndex,
                                                                popupState = popupState,
                                                                onEvent = onEvent,
                                                        )
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                }
        }
}
