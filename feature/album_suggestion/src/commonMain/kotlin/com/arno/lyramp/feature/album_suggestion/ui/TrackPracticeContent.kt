package com.arno.lyramp.feature.album_suggestion.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.album_learning.resources.Res
import com.arno.lyramp.feature.album_learning.resources.album_learning_save_and_next
import com.arno.lyramp.feature.album_learning.resources.album_learning_track_words
import com.arno.lyramp.feature.album_learning.resources.album_practice_all_words
import com.arno.lyramp.feature.album_learning.resources.album_practice_mode_cards
import com.arno.lyramp.feature.album_learning.resources.album_practice_mode_learn
import com.arno.lyramp.feature.album_learning.resources.album_practice_mode_reanalyze
import com.arno.lyramp.feature.album_learning.resources.album_practice_mode_test
import com.arno.lyramp.feature.album_learning.resources.album_practice_progress
import com.arno.lyramp.feature.album_learning.resources.album_words_all_album
import com.arno.lyramp.feature.album_learning.resources.album_words_deselect_all
import com.arno.lyramp.feature.album_learning.resources.album_words_learn_count
import com.arno.lyramp.feature.album_learning.resources.album_words_select_all
import com.arno.lyramp.feature.album_suggestion.presentation.AlbumSuggestionUiState
import com.arno.lyramp.ui.BackButton
import com.arno.lyramp.ui.FlashcardItem
import com.arno.lyramp.ui.FlashcardPager
import com.arno.lyramp.ui.PracticeModeGrid
import com.arno.lyramp.ui.PracticeModeItem
import com.arno.lyramp.ui.WordItem
import com.arno.lyramp.ui.WordProgressRow
import com.arno.lyramp.ui.WordSelectionList
import com.arno.lyramp.ui.theme.LyraColors
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal enum class PracticeMode(val labelRes: StringResource, val icon: String) {
        CARDS(Res.string.album_practice_mode_cards, "🃏"),
        CRAM(Res.string.album_practice_mode_learn, "✏️"),
        TEST(Res.string.album_practice_mode_test, "✅"),
        REANALYZE(Res.string.album_practice_mode_reanalyze, "🔎")
}

@Composable
internal fun TrackPracticeContent(
        state: AlbumSuggestionUiState.TrackPractice,
        onBack: () -> Unit,
        onOpenMode: (String) -> Unit,
        onReanalyze: () -> Unit
) {
        LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                item { TrackHeader(state, onBack) }
                item { TrackProgressBar(state) }

                if (state.words.isNotEmpty()) {
                        item {
                                Spacer(modifier = Modifier.height(4.dp))
                                val cards = remember(state.words) {
                                        state.words.map { w ->
                                                FlashcardItem(
                                                        id = w.id,
                                                        front = w.word,
                                                        back = w.translation,
                                                        subtitle = w.lyricLine,
                                                        progress = w.progress
                                                )
                                        }
                                }
                                FlashcardPager(cards = cards)
                        }
                }

                item {
                        Spacer(modifier = Modifier.height(4.dp))
                        val modes = PracticeMode.entries.map {
                                PracticeModeItem(
                                        id = it.name,
                                        label = stringResource(it.labelRes),
                                        icon = it.icon
                                )
                        }
                        PracticeModeGrid(
                                modes = modes,
                                onModeClick = { id ->
                                        if (id == PracticeMode.REANALYZE.name) onReanalyze()
                                        else onOpenMode(id)
                                }
                        )
                }

                item { WordsListHeader(count = state.words.size) }

                items(state.words, key = { it.id }) { word ->
                        WordProgressRow(
                                word = word.word,
                                translation = word.translation,
                                progress = word.progress
                        )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
        }
}

@Composable
private fun TrackHeader(state: AlbumSuggestionUiState.TrackPractice, onBack: () -> Unit) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                BackButton(onClick = onBack)
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = state.trackTitle,
                                fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Text(
                                text = stringResource(
                                        Res.string.album_practice_progress,
                                        state.learnedInTrack,
                                        state.totalInTrack
                                ),
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                        )
                }
        }
}

@Composable
private fun TrackProgressBar(state: AlbumSuggestionUiState.TrackPractice) {
        val frac = if (state.totalInTrack > 0)
                state.learnedInTrack.toFloat() / state.totalInTrack else 0f
        LinearProgressIndicator(
                progress = { frac },
                modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                color = LyraColors.Correct,
                trackColor = Color.White.copy(alpha = 0.15f)
        )
}

@Composable
private fun WordsListHeader(count: Int) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                        text = stringResource(Res.string.album_practice_all_words),
                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                        color = Color.White
                )
                Text(
                        text = "$count",
                        fontSize = 13.sp, color = Color.White.copy(alpha = 0.5f)
                )
        }
}


@Composable
internal fun TrackWordsContent(
        state: AlbumSuggestionUiState.TrackWordsList,
        onToggleKnown: (String) -> Unit,
        onToggleSelectAll: () -> Unit,
        onSave: () -> Unit,
        onBack: () -> Unit
) {
        val selectedCount = state.words.size - state.knownWords.size
        val selectedKeys = remember(state.words, state.knownWords) {
                state.words.filter { it.word !in state.knownWords }.map { it.word }.toSet()
        }
        val allSelected = selectedCount == state.words.size

        Column(modifier = Modifier.fillMaxSize()) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        BackButton(onClick = onBack)
                        Text(
                                text = if (state.isAlbumMode) stringResource(Res.string.album_words_all_album)
                                else stringResource(Res.string.album_learning_track_words, state.trackTitle),
                                fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                }
                Spacer(modifier = Modifier.height(8.dp))

                WordSelectionList(
                        words = state.words.map { w ->
                                WordItem(
                                        word = w.word,
                                        subtitle = w.lyricLine,
                                        levelTag = w.cefrLevel?.name
                                )
                        },
                        selectedWords = selectedKeys,
                        onToggleWord = { onToggleKnown(it) },
                        onSave = onSave,
                        saveButtonText = stringResource(Res.string.album_learning_save_and_next),
                        saveEnabled = true,
                        headerContent = {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                text = if (allSelected)
                                                        stringResource(Res.string.album_words_deselect_all)
                                                else
                                                        stringResource(Res.string.album_words_select_all),
                                                fontSize = 13.sp,
                                                color = LyraColors.Correct.copy(alpha = 0.8f),
                                                modifier = Modifier.clickable { onToggleSelectAll() }
                                        )
                                        Text(
                                                text = stringResource(Res.string.album_words_learn_count, selectedCount),
                                                fontSize = 13.sp,
                                                color = LyraColors.Correct.copy(alpha = 0.8f)
                                        )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                        }
                )
        }
}
