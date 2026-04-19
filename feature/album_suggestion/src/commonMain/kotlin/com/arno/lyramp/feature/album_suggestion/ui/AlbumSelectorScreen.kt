package com.arno.lyramp.feature.album_suggestion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.arno.lyramp.feature.album_learning.resources.Res
import com.arno.lyramp.feature.album_learning.resources.album_selector_card_tracks
import com.arno.lyramp.feature.album_learning.resources.album_selector_empty_desc
import com.arno.lyramp.feature.album_learning.resources.album_selector_empty_title
import com.arno.lyramp.feature.album_learning.resources.album_selector_load_error
import com.arno.lyramp.feature.album_learning.resources.album_selector_loading
import com.arno.lyramp.feature.album_learning.resources.album_selector_title
import com.arno.lyramp.feature.album_suggestion.presentation.AlbumSelectorItem
import com.arno.lyramp.feature.album_suggestion.presentation.AlbumSelectorScreenModel
import com.arno.lyramp.feature.album_suggestion.presentation.AlbumSelectorUiState
import com.arno.lyramp.ui.BackButton
import com.arno.lyramp.ui.EmptyStateCard
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.OnboardingBackground
import org.jetbrains.compose.resources.stringResource

object AlbumSelectorScreen : Screen {
        @Composable
        override fun Content() {
                val screenModel = getScreenModel<AlbumSelectorScreenModel>()
                val uiState by screenModel.uiState.collectAsState()
                val navigator = LocalNavigator.currentOrThrow

                Box(Modifier.fillMaxSize()) {
                        OnboardingBackground(Modifier.fillMaxSize())
                        Column(
                                Modifier
                                        .fillMaxSize()
                                        .statusBarsPadding()
                                        .navigationBarsPadding()
                                        .padding(horizontal = 20.dp)
                        ) {
                                Spacer(Modifier.height(16.dp))
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                ) {
                                        BackButton(onClick = { navigator.pop() })
                                        Text(
                                                text = stringResource(Res.string.album_selector_title),
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                        )
                                }
                                Spacer(Modifier.height(16.dp))
                                when (val state = uiState) {
                                        is AlbumSelectorUiState.Loading ->
                                                Box(Modifier.fillMaxSize(), Alignment.Center) {
                                                        LoadingCard(stringResource(Res.string.album_selector_loading))
                                                }

                                        is AlbumSelectorUiState.Empty ->
                                                Box(Modifier.fillMaxSize(), Alignment.Center) {
                                                        EmptyStateCard(
                                                                icon = "🎵",
                                                                title = stringResource(Res.string.album_selector_empty_title),
                                                                subtitle = stringResource(Res.string.album_selector_empty_desc)
                                                        )
                                                }

                                        is AlbumSelectorUiState.AlbumsList ->
                                                LazyColumn(
                                                        Modifier.fillMaxSize(),
                                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                        items(state.albums, key = { it.albumId }) { album ->
                                                                AlbumCard(album) {
                                                                        navigator.push(AlbumLearningScreen(album.albumId))
                                                                }
                                                        }
                                                        item { Spacer(Modifier.height(80.dp)) }
                                                }

                                        is AlbumSelectorUiState.Error ->
                                                Box(Modifier.fillMaxSize(), Alignment.Center) {
                                                        ErrorCard(
                                                                message = state.message.ifBlank {
                                                                        stringResource(Res.string.album_selector_load_error)
                                                                },
                                                                onRetry = { screenModel.load() }
                                                        )
                                                }
                                }
                        }
                }
        }
}

@Composable
private fun AlbumCard(album: AlbumSelectorItem, onClick: () -> Unit) {
        Row(
                Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
                AsyncImage(
                        model = album.imageUrl,
                        contentDescription = album.albumTitle,
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                )
                Column(Modifier.weight(1f)) {
                        Text(
                                album.albumTitle,
                                fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                                color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Text(
                                album.artistName,
                                fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f),
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        if (album.totalTracks != null && album.totalTracks > 0) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                        text = stringResource(
                                                Res.string.album_selector_card_tracks,
                                                0,
                                                album.totalTracks
                                        ),
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.4f)
                                )
                        }
                }
        }
}
