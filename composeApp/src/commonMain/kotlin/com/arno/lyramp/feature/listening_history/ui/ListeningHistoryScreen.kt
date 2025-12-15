package com.arno.lyramp.feature.listening_history.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryScreenModel
import com.arno.lyramp.feature.lyrics.ui.LyricsScreen
import org.koin.compose.koinInject

internal object ShowListeningHistoryScreen : Screen {

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val screenModel: ListeningHistoryScreenModel = koinInject()
                val uiState by screenModel.uiState.collectAsState()
                Scaffold { padding ->
                        Box(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .padding(padding),
                                contentAlignment = Alignment.Center
                        ) {
                                when (val state = uiState) {
                                        is ListeningHistoryUiState.Loading -> {
                                                CircularProgressIndicator()
                                        }

                                        is ListeningHistoryUiState.Empty -> {
                                                Text(
                                                        text = "Нет сохранённых треков",
                                                        style = MaterialTheme.typography.bodyLarge
                                                )
                                        }

                                        is ListeningHistoryUiState.Error -> {
                                                ErrorContent(message = state.message)
                                        }

                                        is ListeningHistoryUiState.Success -> {
                                                TrackList(
                                                        tracks = state.tracks,
                                                        onTrackClick = { track ->
                                                                navigator.push(
                                                                        LyricsScreen(
                                                                                artist = track.artists.firstOrNull()
                                                                                        ?: "",
                                                                                songName = track.name
                                                                        )
                                                                )
                                                        }
                                                )
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun ErrorContent(message: String) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
        ) {
                Text(
                        text = "Ошибка загрузки",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
        }
}
