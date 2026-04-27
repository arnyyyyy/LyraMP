package com.arno.lyramp.feature.extraction.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.extraction.presentation.ExtractionScreenModel
import com.arno.lyramp.feature.extraction.presentation.ExtractionUiState
import com.arno.lyramp.feature.extraction.resources.Res
import com.arno.lyramp.feature.extraction.resources.extraction_title
import com.arno.lyramp.feature.extraction.resources.extraction_subtitle
import com.arno.lyramp.feature.extraction.resources.extraction_no_recommendations
import com.arno.lyramp.feature.extraction.resources.extraction_start
import com.arno.lyramp.feature.extraction.resources.extraction_running
import com.arno.lyramp.feature.extraction.resources.extraction_saving
import com.arno.lyramp.feature.extraction.resources.extraction_saving_progress
import com.arno.lyramp.feature.extraction.resources.extraction_done_title
import com.arno.lyramp.feature.extraction.resources.extraction_done_words
import com.arno.lyramp.feature.extraction.resources.extraction_done
import com.arno.lyramp.feature.extraction.resources.retry
import com.arno.lyramp.ui.OnboardingBackground
import com.arno.lyramp.ui.BackButton
import com.arno.lyramp.ui.EmptyStateCard
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LyraFilledButton
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import org.jetbrains.compose.resources.stringResource

object ExtractionVoyagerScreen : Screen {
        @Composable
        override fun Content() {
                val screenModel = getScreenModel<ExtractionScreenModel>()
                val uiState by screenModel.uiState.collectAsState()
                val navigator = LocalNavigator.currentOrThrow

                Box(modifier = Modifier.fillMaxSize()) {
                        OnboardingBackground(modifier = Modifier.fillMaxSize())

                        Column(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .statusBarsPadding()
                                        .navigationBarsPadding()
                                        .padding(horizontal = 20.dp)
                        ) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        BackButton(onClick = { navigator.pop() })
                                        Text(
                                                text = stringResource(Res.string.extraction_title),
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                        )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = when (uiState) {
                                                is ExtractionUiState.WordSelection -> Alignment.TopStart
                                                else -> Alignment.Center
                                        }
                                ) {
                                        when (val state = uiState) {
                                                is ExtractionUiState.Idle ->
                                                        IdleContent(onStart = { screenModel.startExtraction() })

                                                is ExtractionUiState.Running -> {
                                                        val message = if (state.currentTrack.isNotEmpty()) {
                                                                "${stringResource(Res.string.extraction_running)}\n🎵 ${state.currentTrack} "
                                                        } else {
                                                                stringResource(Res.string.extraction_running)
                                                        }
                                                        LoadingCard(
                                                                message = message,
                                                                progress = state.progress
                                                        )
                                                }

                                                is ExtractionUiState.WordSelection -> {
                                                        WordSelectionContent(
                                                                state = state,
                                                                onToggleWord = { screenModel.toggleWord(it) },
                                                                onToggleSelectAll = { screenModel.toggleSelectAll() },
                                                                onSave = { screenModel.saveSelectedWords() }
                                                        )
                                                }

                                                is ExtractionUiState.Saving -> LoadingCard(
                                                        message = if (state.total > 0)
                                                                stringResource(Res.string.extraction_saving_progress, state.saved, state.total)
                                                        else
                                                                stringResource(Res.string.extraction_saving),
                                                        progress = if (state.total > 0) state.saved.toFloat() / state.total else null,
                                                )

                                                is ExtractionUiState.Done -> {
                                                        DoneContent(
                                                                savedCount = state.savedCount,
                                                                onBack = { navigator.pop() }
                                                        )
                                                }


                                                is ExtractionUiState.Error -> ErrorCard(
                                                        message = state.message,
                                                        retryLabel = stringResource(Res.string.retry),
                                                        onRetry = { screenModel.startExtraction() },
                                                )
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun IdleContent(onStart: () -> Unit) {
        EmptyStateCard(
                icon = "🎵",
                title = stringResource(Res.string.extraction_no_recommendations),
                subtitle = stringResource(Res.string.extraction_subtitle),
                actionLabel = stringResource(Res.string.extraction_start),
                onAction = onStart,
        )
}

@Composable
private fun DoneContent(savedCount: Int, onBack: () -> Unit) {
        Column(
                modifier = Modifier.fillMaxWidth().widthIn(max = 500.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Text(text = "✅", fontSize = 48.sp, textAlign = TextAlign.Center)

                Text(
                        text = stringResource(Res.string.extraction_done_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                )

                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                                .padding(20.dp)
                ) {
                        Text(
                                text = stringResource(Res.string.extraction_done_words, savedCount),
                                fontSize = 16.sp,
                                color = LyraColors.Correct
                        )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LyraFilledButton(
                        text = stringResource(Res.string.extraction_done),
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = LyraColorScheme.surface,
                        contentColor = LyraColorScheme.onSurface,
                        height = 56.dp
                )
        }
}
