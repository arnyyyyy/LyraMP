package com.arno.lyramp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.ui.theme.LyraColors

/**
 * Horizontal pager of flashcards. Tap to flip front/back.
 */
@Composable
fun FlashcardPager(
        cards: List<FlashcardItem>,
        modifier: Modifier = Modifier,
        flipHint: String = "Нажми, чтобы перевернуть"
) {
        if (cards.isEmpty()) return
        val pagerState = rememberPagerState(pageCount = { cards.size })

        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                ) { page ->
                        val card = cards[page]
                        var flipped by remember(card.id) { mutableStateOf(false) }

                        Box(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 8.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = if (flipped) 0.18f else 0.12f))
                                        .pointerInput(card.id) {
                                                detectTapGestures(onTap = { flipped = !flipped })
                                        }
                                        .padding(20.dp),
                                contentAlignment = Alignment.Center
                        ) {
                                Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                        Text(
                                                text = if (!flipped) card.front else card.back,
                                                fontSize = 28.sp, fontWeight = FontWeight.Bold,
                                                color = Color.White, textAlign = TextAlign.Center,
                                                maxLines = 3, overflow = TextOverflow.Ellipsis
                                        )
                                        if (flipped && card.subtitle.isNotBlank()) {
                                                Text(
                                                        text = "«${card.subtitle}»",
                                                        fontSize = 13.sp,
                                                        color = Color.White.copy(alpha = 0.55f),
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 2, overflow = TextOverflow.Ellipsis
                                                )
                                        }
                                        if (!flipped) {
                                                Text(
                                                        text = flipHint,
                                                        fontSize = 11.sp,
                                                        color = Color.White.copy(alpha = 0.35f)
                                                )
                                        }
                                }
                        }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = "${pagerState.currentPage + 1} / ${cards.size}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f)
                )
        }
}

/**
 * 2-column grid of practice mode buttons.
 */
@Composable
fun PracticeModeGrid(
        modes: List<PracticeModeItem>,
        onModeClick: (String) -> Unit,
        modifier: Modifier = Modifier
) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                modes.chunked(2).forEach { row ->
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                                row.forEach { mode ->
                                        PracticeModeCard(
                                                mode = mode,
                                                modifier = Modifier.weight(1f),
                                                onClick = { onModeClick(mode.id) }
                                        )
                                }
                                // Fill if odd number
                                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                }
        }
}

@Composable
private fun PracticeModeCard(
        mode: PracticeModeItem,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
) {
        Column(
                modifier = modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable(onClick = onClick)
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
                Text(text = mode.icon, fontSize = 26.sp)
                Text(
                        text = mode.label,
                        fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White,
                        textAlign = TextAlign.Center
                )
        }
}

/**
 * A single word row with progress bar, for the "all track words" section.
 */
@Composable
fun WordProgressRow(
        word: String,
        translation: String,
        progress: Float,
        modifier: Modifier = Modifier
) {
        Column(
                modifier = modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = word,
                                fontSize = 16.sp, fontWeight = FontWeight.Medium,
                                color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                        )
                        Text(
                                text = "${(progress * 100).toInt()}%",
                                fontSize = 12.sp, fontWeight = FontWeight.Medium,
                                color = if (progress >= 1f) LyraColors.Correct else Color.White.copy(alpha = 0.6f)
                        )
                }
                if (translation.isNotBlank()) {
                        Text(
                                text = translation,
                                fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f),
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp)),
                        color = if (progress >= 1f) LyraColors.Correct else LyraColors.Correct.copy(alpha = 0.7f),
                        trackColor = Color.White.copy(alpha = 0.12f)
                )
        }
}

