package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.arno.lyramp.core.ui.SlowModeButton

@Composable
internal fun LyricsWord(
        word: String,
        lyricLine: String,
        lineIndex: Int,
        wordIndex: Int,
        popupState: WordPopupState,
        onEvent: (LyricsEvent) -> Unit,
) {
        val isPopupVisible = popupState is WordPopupState.Visible
                && popupState.lineIndex == lineIndex
                && popupState.wordIndex == wordIndex

        Box {
                Text(
                        text = word,
                        fontSize = 17.sp,
                        fontWeight = if (isPopupVisible) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isPopupVisible) Color(0xFF4A90E2) else Color(0xFF2C3E50),
                        lineHeight = 28.sp,
                        softWrap = true,
                        modifier = Modifier
                                .clickable {
                                        if (word.isNotBlank()) {
                                                if (isPopupVisible) onEvent(LyricsEvent.PopupDismissed)
                                                else onEvent(LyricsEvent.WordTapped(word, lyricLine, lineIndex, wordIndex))
                                        }
                                }
                                .padding(end = 5.dp, bottom = 2.dp)
                )

                if (isPopupVisible) {
                        WordTranslationPopup(
                                state = popupState,
                                onEvent = onEvent,
                        )
                }
        }
}

@Composable
private fun WordTranslationPopup(
        state: WordPopupState.Visible,
        onEvent: (LyricsEvent) -> Unit,
) {
        Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, 80),
                onDismissRequest = { onEvent(LyricsEvent.PopupDismissed) },
                properties = PopupProperties(focusable = true)
        ) {
                Column(
                        modifier = Modifier
                                .widthIn(min = 200.dp, max = 280.dp)
                                .shadow(8.dp, RoundedCornerShape(12.dp))
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                ) {
                        if (state.isTranslating) {
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                strokeWidth = 2.dp,
                                                color = Color(0xFF4A90E2)
                                        )
                                        Text(text = "...", fontSize = 13.sp, color = Color(0xFF7F8C8D))
                                }
                        } else {
                                Text(
                                        text = state.translationResult.translation ?: "Не найдено",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF2C3E50),
                                        textAlign = TextAlign.Start
                                )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                Button(
                                        onClick = {
                                                if (state.isPlayingAudio) onEvent(LyricsEvent.StopAudioRequested)
                                                else onEvent(LyricsEvent.PlayAudioRequested)
                                        },
                                        modifier = Modifier.size(36.dp),
                                        shape = CircleShape,
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                                containerColor = if (state.isPlayingAudio) Color(0xFF34C759) else Color(0xFF4A90E2),
                                                contentColor = Color.White
                                        ),
                                        enabled = !state.isLoadingAudio && !state.isTranslating
                                ) {
                                        if (state.isLoadingAudio) {
                                                CircularProgressIndicator(
                                                        modifier = Modifier.size(16.dp),
                                                        strokeWidth = 2.dp,
                                                        color = Color.White
                                                )
                                        } else {
                                                Text(text = "🔊", fontSize = 16.sp)
                                        }
                                }

                                SlowModeButton(
                                        isSlowMode = state.isSlowMode,
                                        onClick = { onEvent(LyricsEvent.SlowModeToggled) },
                                        size = 30.dp,
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                OutlinedButton(
                                        onClick = { onEvent(LyricsEvent.PopupDismissed) },
                                        modifier = Modifier.height(30.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color(0xFF7F8C8D)
                                        )
                                ) {
                                        Text(text = "Знаю", fontSize = 12.sp)
                                }

                                Button(
                                        onClick = { onEvent(LyricsEvent.SaveWordRequested) },
                                        modifier = Modifier.height(30.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF4A90E2)
                                        ),
                                        enabled = !state.isTranslating && state.translationResult.translation != null
                                ) {
                                        Text(text = "Учить", fontSize = 12.sp)
                                }
                        }
                }
        }
}
