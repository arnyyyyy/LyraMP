package com.arno.lyramp.feature.album_suggestion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.core.navigation.ScreenFactory
import com.arno.lyramp.feature.album_learning.resources.Res
import com.arno.lyramp.feature.album_learning.resources.album_extraction_progress
import com.arno.lyramp.feature.album_learning.resources.album_learning_back_to_album
import com.arno.lyramp.feature.album_learning.resources.album_learning_level_complete_title
import com.arno.lyramp.feature.album_learning.resources.album_learning_level_complete_words
import com.arno.lyramp.feature.album_learning.resources.album_learning_loading_words
import com.arno.lyramp.feature.album_learning.resources.album_learning_next_level
import com.arno.lyramp.feature.album_learning.resources.album_learning_no_new_words_desc
import com.arno.lyramp.feature.album_learning.resources.album_learning_no_new_words_title
import com.arno.lyramp.feature.album_suggestion.presentation.AlbumSuggestionScreenModel
import com.arno.lyramp.feature.album_suggestion.presentation.AlbumSuggestionUiState
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.LyraFilledButton
import com.arno.lyramp.ui.OnboardingBackground
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

data class AlbumLearningScreen(private val albumId: String) : Screen {

        @Composable
        override fun Content() {
                val screenModel = getScreenModel<AlbumSuggestionScreenModel> { parametersOf(albumId) }
                val uiState by screenModel.uiState.collectAsState()
                val navigator = LocalNavigator.currentOrThrow
                val screenFactory: ScreenFactory = koinInject()

                Box(modifier = Modifier.fillMaxSize()) {
                        OnboardingBackground(modifier = Modifier.fillMaxSize())
                        Column(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .statusBarsPadding()
                                        .navigationBarsPadding()
                        ) {
                                when (val state = uiState) {
                                        is AlbumSuggestionUiState.Loading,
                                        is AlbumSuggestionUiState.LoadingTrackWords -> {
                                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                        LoadingCard(message = stringResource(Res.string.album_learning_loading_words))
                                                }
                                        }

                                        is AlbumSuggestionUiState.ExtractionProgress -> {
                                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                        Column(
                                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                                                modifier = Modifier.padding(horizontal = 40.dp)
                                                        ) {
                                                                Text(
                                                                        text = stringResource(
                                                                                Res.string.album_extraction_progress,
                                                                                state.extracted,
                                                                                state.total
                                                                        ),
                                                                        fontSize = 16.sp,
                                                                        color = Color.White,
                                                                        textAlign = TextAlign.Center
                                                                )
                                                                LinearProgressIndicator(
                                                                        progress = { if (state.total > 0) state.extracted.toFloat() / state.total else 0f },
                                                                        modifier = Modifier
                                                                                .fillMaxWidth()
                                                                                .height(6.dp)
                                                                                .clip(RoundedCornerShape(3.dp)),
                                                                        color = LyraColors.Correct,
                                                                        trackColor = Color.White.copy(alpha = 0.15f)
                                                                )
                                                        }
                                                }
                                        }

                                        is AlbumSuggestionUiState.AlbumOverview ->
                                                AlbumOverviewContent(
                                                        state = state,
                                                        onTrackClick = { screenModel.openTrack(it) },
                                                        onLearnAll = { screenModel.openAlbumWords() },
                                                        onBack = { navigator.pop() }
                                                )

                                        is AlbumSuggestionUiState.TrackWordsList ->
                                                Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                                                        Spacer(Modifier.height(16.dp))
                                                        TrackWordsContent(
                                                                state = state,
                                                                onToggleKnown = { screenModel.toggleWordKnown(it) },
                                                                onToggleSelectAll = { screenModel.toggleSelectAll() },
                                                                onSave = { screenModel.saveWordsAndComplete() },
                                                                onBack = { screenModel.backToOverview() }
                                                        )
                                                }

                                        is AlbumSuggestionUiState.TrackPractice ->
                                                TrackPracticeContent(
                                                        state = state,
                                                        onBack = { screenModel.backToOverview() },
                                                        onOpenMode = { modeName ->
                                                                navigator.push(
                                                                        screenFactory.learnWordsScreenForTrack(
                                                                                modeName = modeName,
                                                                                albumId = state.albumId,
                                                                                trackIndex = state.trackIndex
                                                                        )
                                                                )
                                                        },
                                                        onReanalyze = { screenModel.reanalyzeTrack(state.trackIndex) }
                                                )

                                        is AlbumSuggestionUiState.LevelCompleted ->
                                                Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                                                        Spacer(Modifier.height(16.dp))
                                                        LevelCompletedContent(
                                                                state = state,
                                                                onBackToAlbum = { screenModel.backToOverview() },
                                                                onNextTrack = if (state.hasNextTrack) {
                                                                        { screenModel.openNextTrack(state.trackIndex) }
                                                                } else null
                                                        )
                                                }

                                        is AlbumSuggestionUiState.Error ->
                                                AlbumErrorContent(
                                                        message = state.message,
                                                        onRetry = { screenModel.loadAlbumOverview() },
                                                        onBack = { navigator.pop() }
                                                )
                                }
                        }
                }
        }
}

@Composable
private fun AlbumErrorContent(message: String, onRetry: () -> Unit, onBack: () -> Unit) {
        Box(
                Modifier.fillMaxSize().padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ErrorCard(message = message, onRetry = onRetry)
                        Spacer(Modifier.height(16.dp))
                        LyraFilledButton(
                                text = stringResource(Res.string.album_learning_back_to_album),
                                onClick = onBack,
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White,
                                modifier = Modifier.fillMaxWidth()
                        )
                }
        }
}


@Composable
internal fun LevelCompletedContent(
        state: AlbumSuggestionUiState.LevelCompleted,
        onBackToAlbum: () -> Unit,
        onNextTrack: (() -> Unit)? = null
) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                        modifier = Modifier.fillMaxWidth().widthIn(max = 500.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        if (state.savedCount > 0) {
                                Text(text = "🎉", fontSize = 48.sp, textAlign = TextAlign.Center)
                                Text(
                                        text = stringResource(Res.string.album_learning_level_complete_title),
                                        fontSize = 22.sp, fontWeight = FontWeight.Bold,
                                        color = Color.White, textAlign = TextAlign.Center
                                )
                                Text(
                                        text = state.trackTitle,
                                        fontSize = 16.sp, color = Color.White.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                )
                                Box(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                                                .padding(20.dp)
                                ) {
                                        Text(
                                                text = stringResource(
                                                        Res.string.album_learning_level_complete_words,
                                                        state.savedCount
                                                ),
                                                fontSize = 16.sp, color = LyraColors.Correct
                                        )
                                }
                        } else {
                                Text(text = "✅", fontSize = 48.sp, textAlign = TextAlign.Center)
                                Text(
                                        text = stringResource(Res.string.album_learning_no_new_words_title),
                                        fontSize = 22.sp, fontWeight = FontWeight.Bold,
                                        color = Color.White, textAlign = TextAlign.Center
                                )
                                Text(
                                        text = state.trackTitle,
                                        fontSize = 16.sp, color = Color.White.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                )
                                Text(
                                        text = stringResource(Res.string.album_learning_no_new_words_desc),
                                        fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f),
                                        textAlign = TextAlign.Center
                                )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (onNextTrack != null) {
                                LyraFilledButton(
                                        text = stringResource(Res.string.album_learning_next_level),
                                        onClick = onNextTrack,
                                        modifier = Modifier.fillMaxWidth(),
                                        containerColor = LyraColorScheme.surface,
                                        contentColor = LyraColorScheme.onSurface,
                                        height = 56.dp
                                )
                        }

                        LyraFilledButton(
                                text = stringResource(Res.string.album_learning_back_to_album),
                                onClick = onBackToAlbum,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 80.dp),
                                containerColor = if (onNextTrack != null) Color.White.copy(alpha = 0.15f)
                                else LyraColorScheme.surface,
                                contentColor = if (onNextTrack != null) Color.White
                                else LyraColorScheme.onSurface,
                                height = 56.dp
                        )
                }
        }
}
