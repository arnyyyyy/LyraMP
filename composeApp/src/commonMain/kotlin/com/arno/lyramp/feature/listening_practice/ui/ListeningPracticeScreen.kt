package com.arno.lyramp.feature.listening_practice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeScreenModel
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeUiState
import com.arno.lyramp.feature.listening_practice.domain.ListeningPracticeUseCase
import org.koin.compose.koinInject

internal class ListeningPracticeScreen(private val track: PracticeTrack) : Screen {

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val repository: ListeningPracticeUseCase = koinInject()

                val screenModel = remember {
                        ListeningPracticeScreenModel(
                                track = track,
                                repository = repository
                        )
                }
                val uiState by screenModel.uiState.collectAsState()

                DisposableEffect(Unit) {
                        onDispose {
                                screenModel.onDispose()
                        }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFFF5F3EE))
                        )

                        Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                containerColor = Color.Transparent
                        ) { paddingValues ->
                                Column(
                                        modifier = Modifier
                                                .fillMaxSize()
                                                .padding(paddingValues)
                                ) {
                                        Box(
                                                modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Color.White)
                                                        .border(1.dp, Color(0xFFE8E8E8))
                                                        .padding(16.dp)
                                        ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Box(
                                                                modifier = Modifier
                                                                        .size(40.dp)
                                                                        .background(Color(0xFFF0F0F0), CircleShape)
                                                                        .clickable { navigator.pop() },
                                                                contentAlignment = Alignment.Center
                                                        ) {
                                                                Text(
                                                                        text = "←",
                                                                        fontSize = 20.sp,
                                                                        fontWeight = FontWeight.Normal,
                                                                        color = Color(0xFF2C3E50)
                                                                )
                                                        }

                                                        Spacer(modifier = Modifier.width(16.dp))

                                                        Row(
                                                                modifier = Modifier.weight(1f),
                                                                verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                                Text(text = "🎧", fontSize = 24.sp)
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Column(modifier = Modifier.weight(1f)) {
                                                                        Text(
                                                                                text = track.name,
                                                                                fontSize = 18.sp,
                                                                                fontWeight = FontWeight.SemiBold,
                                                                                color = Color(0xFF2C3E50),
                                                                                maxLines = 1,
                                                                                overflow = TextOverflow.Ellipsis
                                                                        )
                                                                        Spacer(modifier = Modifier.height(2.dp))
                                                                        Text(
                                                                                text = track.artists.joinToString(", "),
                                                                                fontSize = 13.sp,
                                                                                color = Color(0xFF7F8C8D),
                                                                                maxLines = 1,
                                                                                overflow = TextOverflow.Ellipsis
                                                                        )
                                                                }
                                                        }
                                                }
                                        }
                                        Box(
                                                modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(16.dp),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                when (val state = uiState) {
                                                        is ListeningPracticeUiState.Loading -> {
                                                                LoadingContent()
                                                        }

                                                        is ListeningPracticeUiState.Error -> {
                                                                ErrorContent(state.message)
                                                        }

                                                        is ListeningPracticeUiState.Ready -> {
                                                                ReadyContent(
                                                                        state = state,
                                                                        onPlayPause = screenModel::onPlayPauseClick,
                                                                        onRewind = screenModel::onMoveBackClick,
                                                                        onFastForward = screenModel::onMoveForwardClick,
                                                                        onUserInputChange = screenModel::onUserInputChange,
                                                                        onCheck = screenModel::onCheckLine,
                                                                        onSkip = screenModel::onSkipLine,
                                                                        onSwitchMode = screenModel::onSwitchMode,
                                                                        onPlayCurrentLine = screenModel::onPlayCurrentLineClick
                                                                )
                                                        }

                                                        is ListeningPracticeUiState.Completed -> {
                                                                ListeningPracticeCompletedScreen(
                                                                        state = state,
                                                                        onRestart = screenModel::onRestart,
                                                                        onBack = { navigator.pop() }
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}
