package com.arno.lyramp.feature.listening_history.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.feature.listeningHistory.resources.Res
import com.arno.lyramp.feature.listeningHistory.resources.lyrics
import com.arno.lyramp.feature.listeningHistory.resources.practice
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
internal fun TrackList(
        tracks: List<ListeningHistoryMusicTrack>,
        onTrackClick: (ListeningHistoryMusicTrack) -> Unit,
        onPracticeClick: ((ListeningHistoryMusicTrack) -> Unit)? = null,
        onHideTrack: ((ListeningHistoryMusicTrack) -> Unit)? = null,
        scrollToTopToken: Int = 0,
) {
        val listState = rememberLazyListState()

        LaunchedEffect(scrollToTopToken) {
                if (scrollToTopToken > 0) listState.animateScrollToItem(0)
        }

        LazyColumn(
                state = listState,
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                        .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                items(tracks, key = { it.id ?: it.name }) { track ->
                        SwipeableTrackItem(
                                track = track,
                                onLyricsClick = { onTrackClick(track) },
                                onPracticeClick = if (onPracticeClick != null && track.id != null) {
                                        { onPracticeClick(track) }
                                } else null,
                                onHideClick = if (onHideTrack != null) {
                                        { onHideTrack(track) }
                                } else null
                        )
                }

                item {
                        Spacer(modifier = Modifier.height(12.dp))
                }
        }
}


@Composable
private fun SwipeableTrackItem(
        track: ListeningHistoryMusicTrack,
        onLyricsClick: () -> Unit,
        onPracticeClick: (() -> Unit)?,
        onHideClick: (() -> Unit)?,
) {
        val offsetX = remember { Animatable(0f) }
        val coroutineScope = rememberCoroutineScope()
        var isVisible by remember { mutableStateOf(true) }

        AnimatedVisibility(
                visible = isVisible,
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(200))
        ) {
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                ) {
                        Box(
                                modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                                LyraColors.Incorrect.copy(alpha = 0.9f),
                                                RoundedCornerShape(16.dp)
                                        ),
                                contentAlignment = Alignment.CenterEnd
                        ) {
                                Box(
                                        modifier = Modifier
                                                .size(64.dp)
                                                .padding(8.dp)
                                                .clickable {
                                                        coroutineScope.launch {
                                                                offsetX.animateTo(0f, tween(150))
                                                        }
                                                        isVisible = false
                                                        onHideClick?.invoke()
                                                },
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(text = "🗑️", fontSize = 24.sp)
                                }
                        }

                        Box(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                                        .pointerInput(Unit) {
                                                detectHorizontalDragGestures(
                                                        onDragEnd = {
                                                                coroutineScope.launch {
                                                                        if (offsetX.value < -100f) {
                                                                                offsetX.animateTo(
                                                                                        -64.dp.toPx(),
                                                                                        tween(200)
                                                                                )
                                                                        } else {
                                                                                offsetX.animateTo(0f, tween(200))
                                                                        }
                                                                }
                                                        }
                                                ) { change, dragAmount ->
                                                        change.consume()
                                                        coroutineScope.launch {
                                                                val newValue = (offsetX.value + dragAmount)
                                                                        .coerceIn(-64.dp.toPx(), 0f)
                                                                offsetX.snapTo(newValue)
                                                        }
                                                }
                                        }
                        ) {
                                TrackItem(
                                        track = track,
                                        onLyricsClick = onLyricsClick,
                                        onPracticeClick = onPracticeClick,
                                )
                        }
                }
        }
}

@Composable
private fun TrackItem(
        track: ListeningHistoryMusicTrack,
        onLyricsClick: () -> Unit,
        onPracticeClick: (() -> Unit)?,
) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(LyraColorScheme.surface.copy(alpha = 0.95f), RoundedCornerShape(16.dp))
                        .border(1.dp, LyraColors.GlassCardBorder, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                        text = "🎵",
                        fontSize = 28.sp,
                        modifier = Modifier.padding(end = 4.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = track.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyraColorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = track.artists.joinToString(", "),
                                fontSize = 14.sp,
                                color = LyraColorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                        )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (onPracticeClick != null) {
                                Column(
                                        modifier = Modifier
                                                .background(
                                                        LyraColorScheme.primary.copy(alpha = 0.1f),
                                                        RoundedCornerShape(8.dp)
                                                )
                                                .clickable(onClick = onPracticeClick)
                                                .padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Text(text = "🎧", fontSize = 20.sp) // TODO: подумать мб не наушники
                                        Text(
                                                text = stringResource(Res.string.practice),
                                                fontSize = 10.sp,
                                                color = LyraColorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                        )
                                }
                        }

                        Column(
                                modifier = Modifier
                                        .background(LyraColors.TrackLyricsChip.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .clickable(onClick = onLyricsClick)
                                        .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                Text(text = "📝", fontSize = 20.sp)
                                Text(
                                        text = stringResource(Res.string.lyrics),
                                        fontSize = 10.sp,
                                        color = LyraColors.TrackLyricsText,
                                        fontWeight = FontWeight.SemiBold
                                )
                        }
                }
        }
}
