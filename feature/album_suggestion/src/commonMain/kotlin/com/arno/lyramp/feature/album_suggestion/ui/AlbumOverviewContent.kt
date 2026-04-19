package com.arno.lyramp.feature.album_suggestion.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.arno.lyramp.feature.album_learning.resources.Res
import com.arno.lyramp.feature.album_learning.resources.album_learning_title
import com.arno.lyramp.feature.album_learning.resources.album_overview_learn_all
import com.arno.lyramp.feature.album_learning.resources.album_overview_level_label
import com.arno.lyramp.feature.album_learning.resources.album_overview_progress_summary
import com.arno.lyramp.feature.album_learning.resources.album_track_card_no_words
import com.arno.lyramp.feature.album_learning.resources.album_track_card_tap_hint
import com.arno.lyramp.feature.album_suggestion.presentation.AlbumSuggestionUiState
import com.arno.lyramp.feature.album_suggestion.presentation.TrackStats
import com.arno.lyramp.ui.BackButton
import com.arno.lyramp.ui.LyraFilledButton
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min

@Composable
internal fun AlbumOverviewContent(
        state: AlbumSuggestionUiState.AlbumOverview,
        onTrackClick: (Int) -> Unit,
        onLearnAll: () -> Unit,
        onBack: () -> Unit
) {
        val listState = rememberLazyListState()
        val collapseFraction by remember {
                derivedStateOf {
                        if (listState.firstVisibleItemIndex > 0) 1f
                        else min(1f, listState.firstVisibleItemScrollOffset.toFloat() / 600f)
                }
        }

        Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxSize()
                ) {
                        item(key = "header") {
                                AlbumOverviewHeader(
                                        state = state,
                                        collapseFraction = collapseFraction,
                                        onLearnAll = onLearnAll
                                )
                        }
                        items(state.tracks, key = { it.trackIndex }) { track ->
                                TrackCard(track = track, onClick = { onTrackClick(track.trackIndex) })
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                }

                CollapsedTopBar(
                        title = state.albumTitle,
                        collapseFraction = collapseFraction,
                        onBack = onBack
                )
        }
}

@Composable
private fun CollapsedTopBar(
        title: String,
        collapseFraction: Float,
        onBack: () -> Unit
) {
        val topBarAlpha by animateFloatAsState(
                targetValue = if (collapseFraction > 0.6f) 1f else 0f,
                label = "topBarAlpha"
        )
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.45f * topBarAlpha))
                        .graphicsLayer {
                                alpha = if (topBarAlpha > 0.02f) 1f
                                else collapseFraction.coerceAtLeast(0f)
                        }
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                BackButton(onClick = onBack)
                Text(
                        text = if (topBarAlpha > 0.5f) title else stringResource(Res.string.album_learning_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                )
        }
}

@Composable
private fun AlbumOverviewHeader(
        state: AlbumSuggestionUiState.AlbumOverview,
        collapseFraction: Float,
        onLearnAll: () -> Unit
) {
        val coverUrl = state.coverUri?.let { "https://" + it.replace("%%", "400x400") }
        val f = collapseFraction.coerceIn(0f, 1f)

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(56.dp))

                Box(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                        contentAlignment = Alignment.Center
                ) {
                        AsyncImage(
                                model = coverUrl,
                                contentDescription = state.albumTitle,
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .graphicsLayer {
                                                translationY = -f * 120f
                                                val scale = 1f - 0.25f * f
                                                scaleX = scale
                                                scaleY = scale
                                                alpha = 1f - f
                                        }
                                        .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop,
                                colorFilter = ColorFilter.colorMatrix(
                                        ColorMatrix().apply {
                                                setToSaturation(0.3f + state.progressFraction * 0.7f)
                                        }
                                )
                        )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                        text = state.albumTitle,
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().graphicsLayer { alpha = 1f - f }
                )
                Text(
                        text = state.artistName,
                        fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().graphicsLayer { alpha = 1f - f }
                )

                Spacer(modifier = Modifier.height(8.dp))

                AlbumProgressSummary(state = state, fadeFraction = f)

                Spacer(modifier = Modifier.height(12.dp))

                if (state.wordsToLearn > 0) {
                        LyraFilledButton(
                                text = stringResource(Res.string.album_overview_learn_all, state.wordsToLearn),
                                onClick = onLearnAll,
                                modifier = Modifier.fillMaxWidth(),
                                containerColor = LyraColorScheme.surface,
                                contentColor = LyraColorScheme.onSurface,
                                height = 52.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                }
        }
}

@Composable
private fun AlbumProgressSummary(
        state: AlbumSuggestionUiState.AlbumOverview,
        fadeFraction: Float
) {
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { alpha = 1f - (fadeFraction * 0.6f) }
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
                Column {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                Text(
                                        text = stringResource(Res.string.album_overview_level_label, state.levelLabel),
                                        fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                        text = "${state.progressPercent}%",
                                        fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                        color = LyraColors.Correct
                                )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                                progress = { state.progressFraction },
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                color = LyraColors.Correct,
                                trackColor = Color.White.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                                text = stringResource(
                                        Res.string.album_overview_progress_summary,
                                        state.learnedWords,
                                        state.totalWords,
                                        state.wordsToLearn
                                ),
                                fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f)
                        )
                }
        }
}

@Composable
private fun TrackCard(track: TrackStats, onClick: () -> Unit) {
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable(onClick = onClick)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
                Column {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                Text(
                                        text = "${track.trackIndex + 1}.",
                                        fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.5f)
                                )
                                Text(
                                        text = track.title,
                                        fontSize = 15.sp, fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                )
                                TrackStatusBadge(track)
                        }
                        if (track.totalWords > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                        progress = { track.progressFraction },
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp)),
                                        color = if (track.isCompleted) LyraColors.Correct
                                        else LyraColors.Correct.copy(alpha = 0.7f),
                                        trackColor = Color.White.copy(alpha = 0.15f)
                                )
                                if (track.pendingWords > 0) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                                text = stringResource(Res.string.album_track_card_tap_hint),
                                                fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f)
                                        )
                                }
                        } else {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                        text = stringResource(Res.string.album_track_card_no_words),
                                        fontSize = 12.sp, color = Color.White.copy(alpha = 0.3f)
                                )
                        }
                }
        }
}

@Composable
private fun TrackStatusBadge(track: TrackStats) {
        when {
                track.isCompleted ->
                        Text(text = "✓", fontSize = 16.sp, color = LyraColors.Correct)

                track.pendingWords > 0 ->
                        Text(
                                text = "📋 ${track.pendingWords}",
                                fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f)
                        )

                track.totalWords > 0 ->
                        Text(
                                text = "${track.learnedWords}/${track.totalWords}",
                                fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f)
                        )
        }
}
