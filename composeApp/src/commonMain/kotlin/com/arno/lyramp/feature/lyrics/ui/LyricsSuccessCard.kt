package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun ShowLyricsSuccessCard(lyrics: String) {
        val scrollState = rememberScrollState()
        Column(
                modifier = Modifier.Companion
                        .verticalScroll(scrollState)
                        .padding(16.dp)
        ) {
                val lines = lyrics.split("\n")

                lines.forEach { line ->
                        val words = line.split(" ")

                        Row(
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                words.forEach { word ->
                                        LyricsWord(word)
                                }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                }
        }
}