package com.arno.lyramp.feature.listening_history.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arno.lyramp.feature.listening_history.model.MusicTrack

@Composable
internal fun TrackList(
        tracks: List<MusicTrack>,
        onTrackClick: (MusicTrack) -> Unit
) {
        LazyColumn(
                modifier = Modifier.fillMaxSize()
        ) {
                items(tracks) { track ->
                        TrackItem(
                                track = track,
                                onClick = { onTrackClick(track) }
                        )
                }
        }
}

@Composable
private fun TrackItem(
        track: MusicTrack,
        onClick: () -> Unit
) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
                Text(
                        text = track.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = track.artists.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
                track.albumName?.let { albumName ->
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = albumName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                        )
                }
        }
}
