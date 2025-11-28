package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
                Text(
                        text = lyrics,
                        style = MaterialTheme.typography.bodyLarge
                )
        }
}