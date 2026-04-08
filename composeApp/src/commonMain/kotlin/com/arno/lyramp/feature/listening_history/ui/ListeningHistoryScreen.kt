package com.arno.lyramp.feature.listening_history.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arno.lyramp.feature.main.ui.LibraryTab
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryScreenModel
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryUiState
import com.arno.lyramp.feature.lyrics.ui.LyricsScreen
import com.arno.lyramp.ui.OnboardingBackground
import com.arno.lyramp.feature.listening_practice.ui.ListeningPracticeScreen
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import com.arno.lyramp.ui.EmptyStateCard
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import lyramp.composeapp.generated.resources.nav_library
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.history_empty_subtitle
import lyramp.composeapp.generated.resources.history_empty_title
import lyramp.composeapp.generated.resources.history_error_title
import lyramp.composeapp.generated.resources.history_loading
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
internal object ShowListeningHistoryScreen : Screen {

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val screenModel = getScreenModel<ListeningHistoryScreenModel>()
                val uiState by screenModel.uiState.collectAsState()
                val isRefreshing by screenModel.isRefreshing.collectAsState()
                val scrollToTopToken by LibraryTab.scrollToTopToken

                Box(modifier = Modifier.fillMaxSize()) {
                        OnboardingBackground(modifier = Modifier.fillMaxSize())

                        Column(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .statusBarsPadding()
                                        .navigationBarsPadding()
                        ) {
                                Box(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp, vertical = 12.dp)
                                ) {
                                        Text(
                                                text = stringResource(Res.string.nav_library),
                                                fontSize = 36.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                                        )
                                }

                                val p2rState = rememberPullToRefreshState()

                                PullToRefreshBox(
                                        state = p2rState,
                                        isRefreshing = isRefreshing,
                                        onRefresh = { screenModel.refresh() },
                                        modifier = Modifier.fillMaxSize(),
                                        indicator = {
                                                Indicator(
                                                        modifier = Modifier.align(Alignment.TopCenter),
                                                        isRefreshing = isRefreshing,
                                                        containerColor = LyraColorScheme.onPrimary,
                                                        color = LyraColors.Yandex,
                                                        state = p2rState
                                                )
                                        }
                                ) {
                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = when (uiState) {
                                                        is ListeningHistoryUiState.Success -> Alignment.TopCenter
                                                        else -> Alignment.Center
                                                }
                                        ) {
                                                when (val state = uiState) {
                                                        is ListeningHistoryUiState.Loading -> {
                                                                LoadingCard(message = stringResource(Res.string.history_loading))
                                                        }

                                                        is ListeningHistoryUiState.Empty -> {
                                                                EmptyStateCard(
                                                                        icon = "📖",
                                                                        title = stringResource(Res.string.history_empty_title),
                                                                        subtitle = stringResource(Res.string.history_empty_subtitle),
                                                                )
                                                        }

                                                        is ListeningHistoryUiState.Error -> {
                                                                ErrorCard(message = "${stringResource(Res.string.history_error_title)}\n${state.message}")
                                                        }

                                                        is ListeningHistoryUiState.Success -> {
                                                                TrackList(
                                                                        tracks = state.tracks,
                                                                        scrollToTopToken = scrollToTopToken,
                                                                        onTrackClick = { track ->
                                                                                navigator.push(
                                                                                        LyricsScreen(
                                                                                                trackId = track.id,
                                                                                                trackName = track.name,
                                                                                                artists = track.artists,
                                                                                                albumName = track.albumName,
                                                                                                imageUrl = track.imageUrl
                                                                                        )
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
