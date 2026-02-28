package com.arno.lyramp.feature.listening_history.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryScreenModel
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryUiState
import com.arno.lyramp.feature.lyrics.ui.LyricsScreen
import com.arno.lyramp.feature.onboarding.ui.background.OnboardingBackground
import com.arno.lyramp.feature.listening_practice.ui.ListeningPracticeScreen
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import lyramp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

internal object ShowListeningHistoryScreen : Screen {

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val screenModel: ListeningHistoryScreenModel = koinInject()
                val uiState by screenModel.uiState.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                        OnboardingBackground(modifier = Modifier.fillMaxSize())

                        Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                containerColor = Color.Transparent,
                                topBar = {}
                        ) { padding ->
                                Column(
                                        modifier = Modifier
                                                .fillMaxSize()
                                                .padding(padding)
                                ) {
                                        Box(
                                                modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 4.dp, vertical = 12.dp)
                                        ) {
                                                        Text(
                                                                text = stringResource(Res.string.history_title),
                                                        fontSize = 36.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                                                )
                                        }

                                        Box(
                                                modifier = Modifier
                                                        .fillMaxSize(),
                                                contentAlignment = when (uiState) {
                                                        is ListeningHistoryUiState.Success -> Alignment.TopCenter
                                                        else -> Alignment.Center
                                                }
                                        ) {
                                                when (val state = uiState) {
                                                        is ListeningHistoryUiState.Loading -> LoadingContent()
                                                        is ListeningHistoryUiState.Empty -> EmptyContent()
                                                        is ListeningHistoryUiState.Error -> ErrorContent(message = state.message)

                                                        is ListeningHistoryUiState.Success -> {
                                                                TrackList(
                                                                        tracks = state.tracks,
                                                                        onTrackClick = { track ->
                                                                                navigator.push(
                                                                                        LyricsScreen(track)
                                                                                )
                                                                        },
                                                                        onPracticeClick = { track ->
                                                                                val practiceTrack = PracticeTrack(
                                                                                        id = track.id ?: "",
                                                                                        albumId = track.albumId,
                                                                                        name = track.name,
                                                                                        artists = track.artists,
                                                                                        albumName = track.albumName,
                                                                                        imageUrl = track.imageUrl
                                                                                )
                                                                                navigator.push(
                                                                                        ListeningPracticeScreen(practiceTrack)
                                                                                )
                                                                        }
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun LoadingContent() {
        Box(
                modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(0.80f)
                        .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(40.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF4A90E2)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                                text = stringResource(Res.string.history_loading),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray
                        )
                }
        }
}

@Composable
private fun EmptyContent() {
        Box(
                modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(0.85f)
                        .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(40.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Text(
                                text = "📖",
                                fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = stringResource(Res.string.history_empty_title),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = stringResource(Res.string.history_empty_subtitle),
                                fontSize = 15.sp,
                                color = Color.Gray
                        )
                }
        }
}

@Composable
private fun ErrorContent(message: String) {
        Box(
                modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(0.85f)
                        .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(28.dp)
        ) {
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Text(
                                text = "⚠️",
                                fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = stringResource(Res.string.history_error_title),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE74C3C)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = message,
                                fontSize = 16.sp,
                                color = Color.Gray
                        )
                }
        }
}
