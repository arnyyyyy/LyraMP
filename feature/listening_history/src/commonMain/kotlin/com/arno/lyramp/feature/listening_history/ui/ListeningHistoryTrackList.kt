package com.arno.lyramp.feature.listening_history.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.core.model.LyraLang.getLanguageFlag
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.feature.listening_history.model.hasResolvedYandexTrackId
import com.arno.lyramp.feature.listeningHistory.resources.Res
import com.arno.lyramp.feature.listeningHistory.resources.lyrics
import com.arno.lyramp.feature.listeningHistory.resources.practice
import com.arno.lyramp.feature.listeningHistory.resources.search_song
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.ui.theme.LyraNonTransparentSurface
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private const val BUTTON_WIDTH_DP = 68
private const val SWIPE_PANEL_WIDTH_DP = BUTTON_WIDTH_DP * 2
private const val SWIPE_THRESHOLD = 100f
private const val SEARCH_NO_RESULTS_TITLE = "Ничего не найдено"
private const val SEARCH_NO_RESULTS_SUBTITLE = "Попробуйте другой запрос"

@Composable
internal fun TrackList(
        tracks: List<ListeningHistoryMusicTrack>,
        searchQuery: String,
        onSearchQueryChange: (String) -> Unit,
        availableLanguages: List<String> = emptyList(),
        onTrackClick: (ListeningHistoryMusicTrack) -> Unit,
        onPracticeClick: ((ListeningHistoryMusicTrack) -> Unit)? = null,
        onHideTrack: ((ListeningHistoryMusicTrack) -> Unit)? = null,
        onUpdateLanguage: ((ListeningHistoryMusicTrack, String) -> Unit)? = null,
        scrollToTopToken: Int = 0,
) {
        val listState = rememberLazyListState()
        var trackToEditLanguage by remember { mutableStateOf<ListeningHistoryMusicTrack?>(null) }
        val focusManager = LocalFocusManager.current

        LaunchedEffect(scrollToTopToken) {
                if (scrollToTopToken > 0 && searchQuery.isBlank()) {
                        listState.animateScrollToItem(1)
                }
        }
        LaunchedEffect(Unit) {
                if (searchQuery.isBlank()) listState.scrollToItem(1)
        }
        LaunchedEffect(searchQuery) {
                if (searchQuery.isBlank() && listState.firstVisibleItemIndex == 0) {
                        listState.animateScrollToItem(1)
                }
        }

        trackToEditLanguage?.let { track ->
                LanguagePickerSheet(
                        currentLanguage = track.language,
                        availableLanguages = availableLanguages,
                        onLanguageSelected = { language ->
                                onUpdateLanguage?.invoke(track, language)
                                trackToEditLanguage = null
                        },
                        onDismiss = { trackToEditLanguage = null }
                )
        }

        LazyColumn(
                state = listState,
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                        .padding(bottom = 16.dp)
                        .pointerInput(Unit) {
                                awaitEachGesture {
                                        awaitFirstDown(requireUnconsumed = false)
                                        focusManager.clearFocus()
                                }
                        },
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                item(key = "search_bar") {
                        LibrarySearchBar(
                                query = searchQuery,
                                onQueryChange = onSearchQueryChange,
                        )
                }

                if (tracks.isEmpty() && searchQuery.isNotBlank()) {
                        item(key = "search_empty") {
                                SearchEmptyItem()
                        }
                }

                itemsIndexed(
                        tracks,
                        key = { _, track -> track.id ?: "${track.name}||${track.artists.joinToString(",")}" }
                ) { _, track ->
                        SwipeableTrackItem(
                                modifier = Modifier.animateItem(),
                                track = track,
                                onLyricsClick = { onTrackClick(track) },
                                onPracticeClick = if (onPracticeClick != null && track.hasResolvedYandexTrackId()) {
                                        { onPracticeClick(track) }
                                } else null,
                                onHideClick = if (onHideTrack != null) {
                                        { onHideTrack(track) }
                                } else null,
                                onEditLanguageClick = if (onUpdateLanguage != null && availableLanguages.isNotEmpty()) {
                                        { trackToEditLanguage = track }
                                } else null,
                        )
                }

                item {
                        Spacer(modifier = Modifier.height(12.dp))
                }
        }
}

@Composable
private fun LibrarySearchBar(
        query: String,
        onQueryChange: (String) -> Unit,
) {
        TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                        .background(LyraNonTransparentSurface, RoundedCornerShape(14.dp))
                        .border(1.dp, LyraColors.GlassCardBorder, RoundedCornerShape(14.dp)),
                placeholder = {
                        Text(
                                text = stringResource(Res.string.search_song),
                                color = LyraColors.TextPlaceholder,
                                fontSize = 15.sp,
                        )
                },
                leadingIcon = {
                        Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = LyraColors.TextSubtle,
                        )
                },
                trailingIcon = {
                        if (query.isNotEmpty()) {
                                IconButton(onClick = { onQueryChange("") }) {
                                        Icon(
                                                imageVector = Icons.Filled.Clear,
                                                contentDescription = "clear",
                                                tint = LyraColors.TextSubtle,
                                        )
                                }
                        }
                },
                singleLine = true,
                textStyle = TextStyle(fontSize = 15.sp, color = LyraColorScheme.onSurface),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(14.dp),
        )
}

@Composable
private fun SwipeableTrackItem(
        modifier: Modifier = Modifier,
        track: ListeningHistoryMusicTrack,
        onLyricsClick: () -> Unit,
        onPracticeClick: (() -> Unit)?,
        onHideClick: (() -> Unit)?,
        onEditLanguageClick: (() -> Unit)?,
) {
        val offsetX = remember { Animatable(0f) }
        val coroutineScope = rememberCoroutineScope()

        Box(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))) {
                Row(
                        modifier = Modifier.matchParentSize(),
                        horizontalArrangement = Arrangement.End
                ) {
                        Box(
                                modifier = Modifier
                                        .fillMaxHeight()
                                        .width(BUTTON_WIDTH_DP.dp)
                                        .background(LyraColorScheme.primary.copy(alpha = 0.9f))
                                        .clickable {
                                                coroutineScope.launch {
                                                        offsetX.animateTo(0f, tween(150))
                                                }
                                                onEditLanguageClick?.invoke()
                                        },
                                contentAlignment = Alignment.Center
                        ) {
                                Text(text = "🌍", fontSize = 24.sp)
                        }

                        Box(
                                modifier = Modifier
                                        .fillMaxHeight()
                                        .width(BUTTON_WIDTH_DP.dp)
                                        .background(LyraColors.Incorrect.copy(alpha = 0.9f))
                                        .clickable {
                                                coroutineScope.launch {
                                                        offsetX.animateTo(0f, tween(150))
                                                }
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
                                                                if (offsetX.value < -SWIPE_THRESHOLD) {
                                                                        offsetX.animateTo(
                                                                                -SWIPE_PANEL_WIDTH_DP.dp.toPx(),
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
                                                                .coerceIn(-SWIPE_PANEL_WIDTH_DP.dp.toPx(), 0f)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun LanguagePickerSheet(
        currentLanguage: String?,
        availableLanguages: List<String>,
        onLanguageSelected: (String) -> Unit,
        onDismiss: () -> Unit,
) {
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
                onDismissRequest = onDismiss,
                sheetState = sheetState,
                containerColor = LyraColorScheme.surface,
        ) {
                Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(bottom = 32.dp)
                                .navigationBarsPadding(),
                ) {
                        Text(
                                text = "Язык трека",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyraColorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 20.dp)
                        )

                        FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                                availableLanguages.forEach { language ->
                                        val isSelected = language == currentLanguage
                                        Box(
                                                modifier = Modifier
                                                        .size(56.dp)
                                                        .background(
                                                                if (isSelected) LyraColorScheme.primary.copy(alpha = 0.2f)
                                                                else LyraColorScheme.surfaceVariant.copy(alpha = 0.6f),
                                                                CircleShape
                                                        )
                                                        .border(
                                                                width = if (isSelected) 2.dp else 1.dp,
                                                                color = if (isSelected) LyraColorScheme.primary
                                                                else LyraColors.GlassCardBorder,
                                                                shape = CircleShape
                                                        )
                                                        .clickable { onLanguageSelected(language) },
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Text(
                                                        text = getLanguageFlag(language),
                                                        fontSize = 26.sp
                                                )
                                        }
                                }
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
                        .background(LyraNonTransparentSurface, RoundedCornerShape(16.dp))
                        .border(1.dp, LyraColors.GlassCardBorder, RoundedCornerShape(16.dp))
                        .clickable(onClick = onLyricsClick)
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
                                maxLines = 2,
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
                                val practiceModifier = Modifier
                                        .background(
                                                LyraColorScheme.primary.copy(alpha = 0.1f),
                                                RoundedCornerShape(8.dp)
                                        )
                                        .let {
                                                if (track.hasSyncedLyrics) {
                                                        it.border(
                                                                width = 1.5.dp,
                                                                color = LyraColors.PracticeBorderSynced,
                                                                shape = RoundedCornerShape(8.dp),
                                                        )
                                                } else it
                                        }
                                        .clickable(onClick = onPracticeClick)
                                        .padding(8.dp)
                                Column(
                                        modifier = practiceModifier,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Text(text = "🎧", fontSize = 20.sp)
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

@Composable
private fun SearchEmptyItem() {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                Text(
                        text = SEARCH_NO_RESULTS_TITLE,
                        style = TextStyle(
                                color = LyraColorScheme.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 32.dp)
                )
                Text(
                        text = SEARCH_NO_RESULTS_SUBTITLE,
                        style = TextStyle(
                                color = LyraColorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 32.dp)
                )
        }
}
