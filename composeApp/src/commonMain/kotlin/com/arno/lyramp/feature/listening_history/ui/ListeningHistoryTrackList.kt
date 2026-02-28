package com.arno.lyramp.feature.listening_history.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.listening_history.model.MusicTrack
import lyramp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TrackList(
        tracks: List<MusicTrack>,
        onTrackClick: (MusicTrack) -> Unit,
        onPracticeClick: ((MusicTrack) -> Unit)? = null,
) {
        LazyColumn(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                items(tracks) { track ->
                        TrackItem(
                                track = track,
                                onLyricsClick = { onTrackClick(track) },
                                onPracticeClick = if (onPracticeClick != null && track.id != null) {
                                        { onPracticeClick(track) }
                                } else null
                        )
                }

                item {
                        Spacer(modifier = Modifier.height(12.dp))
                }
        }
}

@Composable
private fun TrackItem(
        track: MusicTrack,
        onLyricsClick: () -> Unit,
        onPracticeClick: (() -> Unit)?,
) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                        text = "🎵",
                        fontSize = 28.sp,
                        modifier = Modifier.padding(end = 4.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                        modifier = Modifier.weight(1f)
                ) {
                        Text(
                                text = track.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = track.artists.joinToString(", "),
                                fontSize = 14.sp,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                        )
                        track.albumName?.let { albumName ->
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                        text = albumName,
                                        fontSize = 12.sp,
                                        color = Color.LightGray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                        }
                }

                Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        if (onPracticeClick != null) {
                                Column(
                                        modifier = Modifier
                                                .background(Color(0xFF4A90E2).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                                .clickable(onClick = onPracticeClick)
                                                .padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Text(text = "🎧", fontSize = 20.sp) // TODO: подумать мб не наушники
                                        Text(
                                                text = stringResource(Res.string.history_track_practice_label),
                                                fontSize = 10.sp,
                                                color = Color(0xFF4A90E2),
                                                fontWeight = FontWeight.SemiBold
                                        )
                                }
                        }

                        Column(
                                modifier = Modifier
                                        .background(Color(0xFFFFCC00).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .clickable(onClick = onLyricsClick)
                                        .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                Text(text = "📝", fontSize = 20.sp)
                                Text(
                                        text = stringResource(Res.string.history_track_lyrics_label),
                                        fontSize = 10.sp,
                                        color = Color(0xFFE6B800),
                                        fontWeight = FontWeight.SemiBold
                                )
                        }
                }
        }
}
