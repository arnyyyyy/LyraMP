package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.lyrics.presentation.LyricsScreenModel
import com.arno.lyramp.feature.lyrics.repository.LyricsGetterRepository
import org.koin.compose.koinInject

internal class ShowLyricsScreen(val artist: String, val songName: String) : Screen {

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val repository: LyricsGetterRepository = koinInject()
                val screenModel = LyricsScreenModel(
                        artist = artist,
                        song = songName,
                        lyricsRepository = repository
                )
                val uiState by screenModel.uiState.collectAsState()
                Scaffold(
                        topBar = {
                                TopAppBar(
                                        title = {
                                                Column {
                                                        Text(
                                                                text = songName,
                                                                style = MaterialTheme.typography.titleMedium
                                                        )
                                                        Text(
                                                                text = artist,
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                }
                                        },
                                        navigationIcon = {
                                                Button(onClick = { navigator.pop() }) {
                                                }
                                        }
                                )
                        }
                ) { paddingValues ->
                        Box(
                                modifier = Modifier.Companion
                                        .fillMaxSize()
                                        .padding(paddingValues),
                                contentAlignment = Alignment.Companion.Center
                        ) {
                                when (val state = uiState) {
                                        is LyricsUiState.Loading -> {
                                                CircularProgressIndicator()
                                        }

                                        is LyricsUiState.Error -> {
                                                ShowLyricsErrorCard(
                                                        error = state.message
                                                )
                                        }

                                        is LyricsUiState.Success -> {
                                                ShowLyricsSuccessCard(
                                                        lyrics = state.lyrics,
                                                )
                                        }
                                }
                        }
                }
        }
}