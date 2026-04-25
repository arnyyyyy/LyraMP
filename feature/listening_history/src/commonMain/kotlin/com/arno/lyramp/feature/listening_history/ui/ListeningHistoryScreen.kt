package com.arno.lyramp.feature.listening_history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.arno.lyramp.core.model.YANDEX_AUTH_URL
import com.arno.lyramp.core.navigation.ScreenFactory
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.presentation.launchAuthUrl
import com.arno.lyramp.feature.authorization.presentation.yandex.YandexAuthBus
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryScreenModel
import com.arno.lyramp.feature.listening_history.presentation.ListeningHistoryUiState
import com.arno.lyramp.ui.OnboardingBackground
import com.arno.lyramp.ui.EmptyStateCard
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.feature.listeningHistory.resources.Res
import com.arno.lyramp.feature.listeningHistory.resources.nav_library
import com.arno.lyramp.feature.listeningHistory.resources.history_empty_subtitle
import com.arno.lyramp.feature.listeningHistory.resources.history_empty_title
import com.arno.lyramp.feature.listeningHistory.resources.history_error_title
import com.arno.lyramp.feature.listeningHistory.resources.history_loading
import com.arno.lyramp.feature.user_settings.presentation.UserSettingsScreenModel
import com.arno.lyramp.feature.user_settings.presentation.UserSettingsScreenModel.Companion.AVAILABLE_LANGUAGES
import com.arno.lyramp.feature.user_settings.ui.UserSettingsSheet
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

object ShowListeningHistoryScreen : Screen {

        val scrollToTopToken: MutableState<Int> = mutableIntStateOf(0)

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val screenModel = getScreenModel<ListeningHistoryScreenModel>()
                val screenFactory: ScreenFactory = koinInject()
                val uiState by screenModel.uiState.collectAsState()
                val isRefreshing by screenModel.isRefreshing.collectAsState()
                val selectedLanguage by screenModel.selectedLanguage.collectAsState()
                val availableLanguages by screenModel.availableLanguages.collectAsState()
                val playlistSources by screenModel.playlistSources.collectAsState()
                val isYandexAuthorized by screenModel.isYandexAuthorized.collectAsState()

                val yandexAuthBus: YandexAuthBus = koinInject()

                LaunchedEffect(Unit) {
                        yandexAuthBus.flow.collect { result ->
                                screenModel.onYandexLoginSuccess(result.token, result.expiresIn)
                                yandexAuthBus.consume()
                        }
                }

                val userSettingsScreenModel: UserSettingsScreenModel = koinInject()
                val settingsState by userSettingsScreenModel.state.collectAsState()
                var showSettingsSheet by remember { mutableStateOf(false) }
                var showAddContentSheet by remember { mutableStateOf(false) }

                if (showSettingsSheet) {
                        UserSettingsSheet(
                                state = settingsState,
                                availableLanguages = AVAILABLE_LANGUAGES,
                                onToggleLanguage = userSettingsScreenModel::toggleLanguage,
                                onSelectLevel = userSettingsScreenModel::selectLevel,
                                onDone = {
                                        userSettingsScreenModel.saveAndClose()
                                        showSettingsSheet = false
                                        screenModel.refreshLanguages()
                                },
                                onDismiss = {
                                        showSettingsSheet = false
                                        screenModel.refreshLanguages()
                                },
                        )
                }

                if (showAddContentSheet) {
                        AddContentSheet(
                                playlistSources = playlistSources,
                                showYandexLoginButton = !isYandexAuthorized,
                                onLoginWithYandex = {
                                        launchAuthUrl(YANDEX_AUTH_URL, MusicServiceType.YANDEX)
                                },
                                onSavePlaylistUrl = { url ->
                                        screenModel.onPlaylistUrlChanged(url)
                                },
                                onAddTrack = { name, artist ->
                                        screenModel.addManualTrack(name, artist)
                                },
                                onRemovePlaylistSource = screenModel::removePlaylistSource,
                                onDismiss = { showAddContentSheet = false },
                        )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                        OnboardingBackground(modifier = Modifier.fillMaxSize())

                        Column(
                                modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                        ) {
                                Row(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 20.dp, vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                text = stringResource(Res.string.nav_library),
                                                fontSize = 36.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.weight(1f)
                                        )

                                        LanguageSelectorWithAddButtonWrapper(
                                                selectedLanguage = selectedLanguage,
                                                availableLanguages = availableLanguages,
                                                onLanguageSelected = { screenModel.selectLanguage(it) },
                                                onSettingsClick = { showSettingsSheet = true },
                                                onAddContentClick = { showAddContentSheet = true },
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
                                                                        availableLanguages = AVAILABLE_LANGUAGES,
                                                                        scrollToTopToken = scrollToTopToken.value,
                                                                        onTrackClick = { track ->
                                                                                navigator.push(
                                                                                        screenFactory.lyricsScreen(
                                                                                                trackId = track.id,
                                                                                                trackName = track.name,
                                                                                                artists = track.artists,
                                                                                                albumName = track.albumName,
                                                                                                imageUrl = track.imageUrl
                                                                                        )
                                                                                )
                                                                        },
                                                                        onPracticeClick = if (screenModel.isPracticeAvailable) { track ->
                                                                                if (track.id != null) {
                                                                                        navigator.push(
                                                                                                screenFactory.listeningPracticeScreen(
                                                                                                        id = track.id,
                                                                                                        albumId = track.albumId,
                                                                                                        name = track.name,
                                                                                                        artists = track.artists,
                                                                                                        albumName = track.albumName,
                                                                                                        imageUrl = track.imageUrl
                                                                                                )
                                                                                        )
                                                                                } else {
                                                                                        null
                                                                                }
                                                                        } else null,
                                                                        onHideTrack = { track ->
                                                                                screenModel.hideTrack(track)
                                                                        },
                                                                        onUpdateLanguage = { track, language ->
                                                                                screenModel.updateTrackLanguage(track, language)
                                                                        },
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}
