package com.arno.lyramp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.core.model.CefrDifficultyGroup

data class VocabularyWordItem(
        val id: Long,
        val word: String,
        val translation: String,
        val isImportant: Boolean,
)

sealed interface VocabularyWordsFilter {
        data object All : VocabularyWordsFilter
        data class Cefr(val group: CefrDifficultyGroup) : VocabularyWordsFilter
        data object ImportantOnly : VocabularyWordsFilter
}

data class VocabularyWordsText(
        val title: String,
        val count: String?,
        val loading: String,
        val emptyTitle: String,
        val emptySubtitle: String?,
        val noFilteredWordsTitle: String,
        val filterAll: String,
        val filterImportant: String,
        val cefrBeginner: String,
        val cefrIntermediate: String,
        val cefrAdvanced: String,
        val audioPlay: String? = null,
        val importantToggle: String,
)

@Composable
fun VocabularyWordsContent(
        texts: VocabularyWordsText,
        allWords: List<VocabularyWordItem>,
        visibleWords: List<VocabularyWordItem>,
        filter: VocabularyWordsFilter,
        availableCefrGroups: Set<CefrDifficultyGroup>,
        cefrByWord: Map<String, CefrDifficultyGroup>,
        isLoading: Boolean,
        loadingAudioWordId: Long? = null,
        playingAudioWordId: Long? = null,
        onBack: () -> Unit,
        onSelectFilter: (VocabularyWordsFilter) -> Unit,
        onToggleImportant: (Long, Boolean) -> Unit,
        onSpeakWord: ((VocabularyWordItem) -> Unit)? = null,
        modifier: Modifier = Modifier,
        bottomPadding: Dp = 0.dp,
) {
        Box(modifier = modifier.fillMaxSize()) {
                OnboardingBackground(modifier = Modifier.fillMaxSize())

                Column(
                        modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                                .padding(bottom = bottomPadding)
                ) {
                        Row(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                BackButton(onClick = onBack)

                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = texts.title,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                        )
                                        texts.count?.let {
                                                Text(
                                                        text = it,
                                                        fontSize = 13.sp,
                                                        color = Color.White.copy(alpha = 0.6f),
                                                )
                                        }
                                }
                        }

                        if (availableCefrGroups.isNotEmpty() || allWords.any { it.isImportant }) {
                                FilterChipsRow(
                                        selected = filter,
                                        availableCefrGroups = availableCefrGroups,
                                        hasImportantWords = allWords.any { it.isImportant },
                                        texts = texts,
                                        onSelect = onSelectFilter,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                        }

                        when {
                                isLoading -> {
                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                LoadingCard(message = texts.loading)
                                        }
                                }

                                allWords.isEmpty() -> {
                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                EmptyStateCard(
                                                        icon = "📖",
                                                        title = texts.emptyTitle,
                                                        subtitle = texts.emptySubtitle,
                                                )
                                        }
                                }

                                visibleWords.isEmpty() -> {
                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                EmptyStateCard(
                                                        icon = "🔎",
                                                        title = texts.noFilteredWordsTitle,
                                                        subtitle = null,
                                                )
                                        }
                                }

                                else -> {
                                        WordList(
                                                words = visibleWords,
                                                cefrByWord = cefrByWord,
                                                loadingAudioWordId = loadingAudioWordId,
                                                playingAudioWordId = playingAudioWordId,
                                                texts = texts,
                                                onToggleImportant = onToggleImportant,
                                                onSpeakWord = onSpeakWord,
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun FilterChipsRow(
        selected: VocabularyWordsFilter,
        availableCefrGroups: Set<CefrDifficultyGroup>,
        hasImportantWords: Boolean,
        texts: VocabularyWordsText,
        onSelect: (VocabularyWordsFilter) -> Unit,
) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
                FilterChip(
                        text = texts.filterAll,
                        isSelected = selected is VocabularyWordsFilter.All,
                        onClick = { onSelect(VocabularyWordsFilter.All) }
                )
                CefrDifficultyGroup.entries.forEach { group ->
                        if (group in availableCefrGroups) {
                                val label = when (group) {
                                        CefrDifficultyGroup.BEGINNER -> texts.cefrBeginner
                                        CefrDifficultyGroup.INTERMEDIATE -> texts.cefrIntermediate
                                        CefrDifficultyGroup.ADVANCED -> texts.cefrAdvanced
                                }
                                FilterChip(
                                        text = "${group.label} · $label",
                                        isSelected = (selected as? VocabularyWordsFilter.Cefr)?.group == group,
                                        onClick = { onSelect(VocabularyWordsFilter.Cefr(group)) }
                                )
                        }
                }
                if (hasImportantWords) {
                        FilterChip(
                                text = "⭐ " + texts.filterImportant,
                                isSelected = selected is VocabularyWordsFilter.ImportantOnly,
                                onClick = { onSelect(VocabularyWordsFilter.ImportantOnly) }
                        )
                }
        }
}

@Composable
private fun FilterChip(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit,
) {
        val bg = if (isSelected) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.12f)
        val fg = if (isSelected) Color(0xFF1C1C1E) else Color.White.copy(alpha = 0.9f)
        Box(
                modifier = Modifier
                        .clickable(onClick = onClick)
                        .background(bg, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
                Text(
                        text = text,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = fg,
                )
        }
}

@Composable
private fun WordList(
        words: List<VocabularyWordItem>,
        cefrByWord: Map<String, CefrDifficultyGroup>,
        loadingAudioWordId: Long?,
        playingAudioWordId: Long?,
        texts: VocabularyWordsText,
        onToggleImportant: (Long, Boolean) -> Unit,
        onSpeakWord: ((VocabularyWordItem) -> Unit)?,
) {
        val listState = rememberLazyListState()
        LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
                items(items = words, key = { it.id }) { word ->
                        WordRow(
                                word = word,
                                cefrGroup = cefrByWord[word.word],
                                isAudioLoading = loadingAudioWordId == word.id,
                                isAudioPlaying = playingAudioWordId == word.id,
                                texts = texts,
                                onToggleImportant = { onToggleImportant(word.id, !word.isImportant) },
                                onSpeak = onSpeakWord?.let { speak -> { speak(word) } },
                        )
                }
        }
}

@Composable
private fun WordRow(
        word: VocabularyWordItem,
        cefrGroup: CefrDifficultyGroup?,
        isAudioLoading: Boolean,
        isAudioPlaying: Boolean,
        texts: VocabularyWordsText,
        onToggleImportant: () -> Unit,
        onSpeak: (() -> Unit)?,
) {
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.10f), RoundedCornerShape(14.dp))
                        .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(14.dp),
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                        Column(modifier = Modifier.weight(1f)) {
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                        Text(
                                                text = word.word,
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White,
                                        )
                                        if (cefrGroup != null) {
                                                CefrBadge(group = cefrGroup)
                                        }
                                }
                                if (word.translation.isNotBlank()) {
                                        Text(
                                                text = word.translation,
                                                fontSize = 14.sp,
                                                color = Color.White.copy(alpha = 0.65f),
                                        )
                                }
                        }

                        if (onSpeak != null && texts.audioPlay != null) {
                                RowActionButton(onClick = onSpeak) {
                                        when {
                                                isAudioLoading -> CircularProgressIndicator(
                                                        modifier = Modifier.size(16.dp),
                                                        color = Color.White.copy(alpha = 0.85f),
                                                        strokeWidth = 2.dp,
                                                )

                                                isAudioPlaying -> Icon(
                                                        imageVector = Icons.Filled.Stop,
                                                        contentDescription = texts.audioPlay,
                                                        tint = Color(0xFF4A90E2),
                                                        modifier = Modifier.size(18.dp),
                                                )

                                                else -> Icon(
                                                        imageVector = Icons.Filled.VolumeUp,
                                                        contentDescription = texts.audioPlay,
                                                        tint = Color.White.copy(alpha = 0.75f),
                                                        modifier = Modifier.size(18.dp),
                                                )
                                        }
                                }
                        }

                        RowActionButton(onClick = onToggleImportant) {
                                Icon(
                                        imageVector = if (word.isImportant) Icons.Filled.Star else Icons.Filled.StarBorder,
                                        contentDescription = texts.importantToggle,
                                        tint = if (word.isImportant) Color(0xFFFFC84A) else Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp),
                                )
                        }
                }
        }
}

@Composable
private fun RowActionButton(
        onClick: () -> Unit,
        content: @Composable () -> Unit,
) {
        IconButton(
                onClick = onClick,
                modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape),
        ) {
                content()
        }
}

@Composable
private fun CefrBadge(group: CefrDifficultyGroup) {
        Box(
                modifier = Modifier
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
                Text(
                        text = group.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.75f),
                )
        }
}
