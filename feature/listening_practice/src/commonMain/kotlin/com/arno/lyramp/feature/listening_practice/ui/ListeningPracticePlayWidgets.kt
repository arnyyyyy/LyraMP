package com.arno.lyramp.feature.listening_practice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.listening_practice.formatTime
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import com.arno.lyramp.ui.SlowModeButton
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors

@Composable
internal fun PlayerControls(
        isPlaying: Boolean,
        currentPositionMs: Long,
        durationMs: Long,
        onPlayPause: () -> Unit,
        onRewind: () -> Unit,
        onFastForward: () -> Unit,
        isSlowMode: Boolean = false,
        onToggleSlowMode: () -> Unit = {},
) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(LyraColorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(16.dp),
        ) {
                val progress = if (durationMs > 0) currentPositionMs.toFloat() / durationMs else 0f
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(LyraColorScheme.outline, RoundedCornerShape(2.dp)),
                ) {
                        Box(
                                modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .height(4.dp)
                                        .background(LyraColorScheme.primary, RoundedCornerShape(2.dp)),
                        )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                        Text(formatTime(currentPositionMs), fontSize = 12.sp, color = LyraColorScheme.onSurfaceVariant)
                        Text(formatTime(durationMs), fontSize = 12.sp, color = LyraColorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                ) {
                        CircleIconButton(label = "⏪", onClick = onRewind)

                        Spacer(modifier = Modifier.width(20.dp))

                        Button(
                                onClick = onPlayPause,
                                modifier = Modifier.size(60.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = LyraColorScheme.primary,
                                        contentColor = LyraColorScheme.onPrimary,
                                ),
                        ) { Text(text = if (isPlaying) "⏸" else "▶", fontSize = 24.sp) }

                        Spacer(modifier = Modifier.width(20.dp))

                        CircleIconButton(label = "⏩", onClick = onFastForward)

                        Spacer(modifier = Modifier.width(12.dp))

                        SlowModeButton(isSlowMode = isSlowMode, onClick = onToggleSlowMode)
                }
        }
}

@Composable
internal fun LinePlayCard(
        track: PracticeTrack?,
        isPlaying: Boolean,
        isPlayerReady: Boolean,
        isSlowMode: Boolean,
        onPlayCurrentLine: () -> Unit,
        onToggleSlowMode: () -> Unit,
        onExpandStart: () -> Unit,
        onExpandEnd: () -> Unit,
        modifier: Modifier = Modifier,
) {
        Box(
                modifier = modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
        ) {
                SlowModeButton(
                        isSlowMode = isSlowMode,
                        onClick = onToggleSlowMode,
                        modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp),
                        size = 36.dp,
                )

                Column(
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                        Text(text = "🎵", fontSize = 18.sp)
                        if (track != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                        text = track.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = LyraColors.TextPrimary,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                )
                                Text(
                                        text = track.artists.joinToString(", "),
                                        fontSize = 12.sp,
                                        color = LyraColors.TextSubtle,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                        ) {
                                SegmentExpandButton(label = "⏪", onClick = onExpandStart)
                                BigPlayButton(isPlaying = isPlaying, isReady = isPlayerReady, onClick = onPlayCurrentLine)
                                SegmentExpandButton(label = "⏩", onClick = onExpandEnd)
                        }
                }
        }
}

@Composable
private fun BigPlayButton(isPlaying: Boolean, isReady: Boolean, onClick: () -> Unit) {
        val bg = when {
                isPlaying -> LyraColors.Correct
                !isReady -> LyraColors.CardSurfaceAlt
                else -> LyraColorScheme.primary
        }
        Box(
                modifier = Modifier
                        .size(68.dp)
                        .background(bg, CircleShape)
                        .clickable(enabled = isReady, onClick = onClick),
                contentAlignment = Alignment.Center,
        ) {
                Text(
                        text = if (isPlaying) "❚❚" else "▶",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isReady) Color.White else LyraColors.TextPlaceholder,
                )
        }
}
