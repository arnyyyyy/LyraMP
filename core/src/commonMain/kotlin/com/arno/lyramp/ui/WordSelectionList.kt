package com.arno.lyramp.ui

import androidx.compose.foundation.background
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors

@Composable
fun WordSelectionList(
        words: List<WordItem>,
        selectedWords: Set<String>,
        onToggleWord: (String) -> Unit,
        onSave: () -> Unit,
        saveButtonText: String,
        saveEnabled: Boolean = selectedWords.isNotEmpty(),
        headerContent: @Composable (() -> Unit)? = null,
        modifier: Modifier = Modifier
) {
        Column(modifier = modifier.fillMaxSize()) {
                headerContent?.invoke()

                LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                        items(words, key = { it.word }) { word ->
                                WordCheckItem(
                                        word = word,
                                        isSelected = word.word in selectedWords,
                                        onToggle = { onToggleWord(word.word) }
                                )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                LyraFilledButton(
                        text = saveButtonText,
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        containerColor = LyraColorScheme.surface,
                        contentColor = LyraColorScheme.onSurface,
                        height = 56.dp,
                        enabled = saveEnabled
                )
        }
}

@Composable
private fun WordCheckItem(
        word: WordItem,
        isSelected: Boolean,
        onToggle: () -> Unit
) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onToggle)
                        .background(
                                if (isSelected) Color.White.copy(alpha = 0.08f) else Color.Transparent,
                                RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
                Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggle() },
                        colors = CheckboxDefaults.colors(
                                checkedColor = LyraColors.Correct,
                                uncheckedColor = Color.White.copy(alpha = 0.4f),
                                checkmarkColor = Color.White
                        )
                )
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = word.word,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
                        )
                        if (word.subtitle.isNotBlank()) {
                                Text(
                                        text = word.subtitle,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.3f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                        }
                }
                if (word.levelTag != null) {
                        Text(
                                text = word.levelTag,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.35f)
                        )
                }
        }
}

data class WordItem(
        val word: String,
        val subtitle: String = "",
        val levelTag: String? = null,
)
