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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun ShowLyricsSuccessCard(lyrics: String) {
        val scrollState = rememberScrollState()

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(16.dp))
                        .padding(24.dp)
        ) {
                Column(
                        modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 8.dp)
                ) {
                        val lines = lyrics.split("\n")

                        lines.forEachIndexed { index, line ->
                                if (line.isBlank()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                } else {
                                        val words = line.split(" ")

                                        FlowRow(
                                                horizontalArrangement = Arrangement.Start,
                                                modifier = Modifier.fillMaxWidth()
                                        ) {
                                                words.forEach { word ->
                                                        LyricsWord(word)
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                }
        }
}