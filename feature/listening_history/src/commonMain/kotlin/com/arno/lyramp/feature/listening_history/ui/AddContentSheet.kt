package com.arno.lyramp.feature.listening_history.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.listeningHistory.resources.Res
import com.arno.lyramp.feature.listeningHistory.resources.add_content_playlist_save
import com.arno.lyramp.feature.listeningHistory.resources.add_content_playlist_section
import com.arno.lyramp.feature.listeningHistory.resources.add_content_playlist_url_label
import com.arno.lyramp.feature.listeningHistory.resources.add_content_title
import com.arno.lyramp.feature.listeningHistory.resources.add_content_track_add
import com.arno.lyramp.feature.listeningHistory.resources.add_content_track_added
import com.arno.lyramp.feature.listeningHistory.resources.add_content_track_artist
import com.arno.lyramp.feature.listeningHistory.resources.add_content_track_name
import com.arno.lyramp.feature.listeningHistory.resources.add_content_track_section
import com.arno.lyramp.ui.LyraFilledButton
import com.arno.lyramp.ui.theme.LyraColorScheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddContentSheet(
        onSavePlaylistUrl: (String) -> Unit,
        onAddTrack: (name: String, artist: String) -> Unit,
        onDismiss: () -> Unit,
) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
                onDismissRequest = onDismiss,
                sheetState = sheetState,
                containerColor = LyraColorScheme.surface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                dragHandle = {
                        Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                contentAlignment = Alignment.Center
                        ) {
                                Box(
                                        modifier = Modifier
                                                .width(40.dp)
                                                .height(4.dp)
                                                .background(LyraColorScheme.outline, RoundedCornerShape(2.dp))
                                )
                        }
                },
        ) {
                AddContentSheetContent(
                        onSavePlaylistUrl = onSavePlaylistUrl,
                        onAddTrack = onAddTrack,
                )
        }
}

@Composable
private fun AddContentSheetContent(
        onSavePlaylistUrl: (String) -> Unit,
        onAddTrack: (name: String, artist: String) -> Unit,
) {
        var playlistUrl by remember { mutableStateOf("") }
        var trackName by remember { mutableStateOf("") }
        var trackArtist by remember { mutableStateOf("") }
        var trackAdded by remember { mutableStateOf(false) }

        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp, bottom = 24.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                ) {
                        Text(text = "➕", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = stringResource(Res.string.add_content_title),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyraColorScheme.onSurface,
                        )
                }

                Spacer(modifier = Modifier.height(28.dp))

                AddContentSectionLabel(text = stringResource(Res.string.add_content_playlist_section))

                OutlinedTextField(
                        value = playlistUrl,
                        onValueChange = { playlistUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(Res.string.add_content_playlist_url_label)) },
                        colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LyraColorScheme.primary,
                                unfocusedBorderColor = LyraColorScheme.outline,
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                )

                Spacer(modifier = Modifier.height(12.dp))

                LyraFilledButton(
                        text = stringResource(Res.string.add_content_playlist_save),
                        onClick = { onSavePlaylistUrl(playlistUrl) },
                        modifier = Modifier.fillMaxWidth(),
                        height = 44.dp,
                        enabled = playlistUrl.isNotBlank(),
                )

                Spacer(modifier = Modifier.height(28.dp))

                AddContentSectionLabel(text = stringResource(Res.string.add_content_track_section))

                OutlinedTextField(
                        value = trackName,
                        onValueChange = { trackName = it; trackAdded = false },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(Res.string.add_content_track_name)) },
                        colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LyraColorScheme.primary,
                                unfocusedBorderColor = LyraColorScheme.outline,
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                        value = trackArtist,
                        onValueChange = { trackArtist = it; trackAdded = false },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(Res.string.add_content_track_artist)) },
                        colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LyraColorScheme.primary,
                                unfocusedBorderColor = LyraColorScheme.outline,
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (trackAdded) {
                        Text(
                                text = stringResource(Res.string.add_content_track_added),
                                fontSize = 14.sp,
                                color = LyraColorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )
                }

                LyraFilledButton(
                        text = stringResource(Res.string.add_content_track_add),
                        onClick = {
                                onAddTrack(trackName.trim(), trackArtist.trim())
                                trackName = ""
                                trackArtist = ""
                                trackAdded = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        height = 44.dp,
                        enabled = trackName.isNotBlank() && trackArtist.isNotBlank(),
                )
        }
}

@Composable
private fun AddContentSectionLabel(text: String) {
        Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = LyraColorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
}
