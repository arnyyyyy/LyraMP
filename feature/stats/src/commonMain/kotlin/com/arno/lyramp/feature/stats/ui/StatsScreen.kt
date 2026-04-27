package com.arno.lyramp.feature.stats.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.stats.presentation.StatsScreenModel
import com.arno.lyramp.feature.stats.presentation.StatsUiState
import com.arno.lyramp.feature.stats.resources.Res
import com.arno.lyramp.feature.stats.resources.stats_error_title
import com.arno.lyramp.feature.stats.resources.stats_loading
import com.arno.lyramp.feature.stats.resources.stats_refresh
import com.arno.lyramp.feature.stats.resources.stats_screen_title
import com.arno.lyramp.ui.BackButton
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.OnboardingBackground
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class StatsScreen(private val language: String?) : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val screenModel = getScreenModel<StatsScreenModel> { parametersOf(language) }
                val state by screenModel.uiState.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                        OnboardingBackground(modifier = Modifier.fillMaxSize())

                        Column(
                                modifier = Modifier.fillMaxSize()
                                        .statusBarsPadding()
                                        .navigationBarsPadding(),
                        ) {
                                TopBar(
                                        onBack = { navigator.pop() },
                                        isRefreshing = (state as? StatsUiState.Ready)?.isRefreshing == true,
                                        onRefresh = screenModel::refresh,
                                )

                                when (val s = state) {
                                        is StatsUiState.Loading -> LoadingCard(message = stringResource(Res.string.stats_loading))
                                        is StatsUiState.Error -> Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center,
                                        ) {
                                                ErrorCard(
                                                        message = "${stringResource(Res.string.stats_error_title)}\n\n${s.message}",
                                                        onRetry = screenModel::retryLoad,
                                                )
                                        }

                                        is StatsUiState.Ready -> StatsContent(snapshot = s.snapshot)
                                }
                        }
                }
        }
}

@Composable
private fun TopBar(
        onBack: () -> Unit,
        isRefreshing: Boolean,
        onRefresh: () -> Unit,
) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
        ) {
                BackButton(onClick = onBack)
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                        text = stringResource(Res.string.stats_screen_title),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                )
                val refreshDesc = stringResource(Res.string.stats_refresh)
                IconButton(onClick = onRefresh, enabled = !isRefreshing) {
                        if (isRefreshing) {
                                CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                )
                        } else {
                                Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = refreshDesc,
                                        tint = Color.White,
                                )
                        }
                }
        }
}
