package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import com.arno.lyramp.feature.lyrics.presentation.LyricsEvent
import com.arno.lyramp.feature.lyrics.presentation.WordPopupState
import com.arno.lyramp.ui.SlowModeButton
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.feature.lyrics.resources.Res
import com.arno.lyramp.feature.lyrics.resources.know
import com.arno.lyramp.feature.lyrics.resources.learn
import com.arno.lyramp.feature.lyrics.resources.loading_dots
import com.arno.lyramp.feature.lyrics.resources.not_found
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LyricsWord(
        word: String,
        isSelected: Boolean,
        isHighlighted: Boolean,
        onClick: () -> Unit,
        onLongClick: () -> Unit,
) {
        Text(
                text = word,
                fontSize = 17.sp,
                fontWeight = if (isSelected || isHighlighted) FontWeight.SemiBold else FontWeight.Normal,
                color = when {
                        isSelected -> Color.White
                        isHighlighted -> LyraColorScheme.primary
                        else -> LyraColorScheme.onSurface
                },
                lineHeight = 28.sp,
                softWrap = true,
                modifier = Modifier
                        .then(
                                if (isSelected) Modifier.background(LyraColorScheme.primary.copy(alpha = 0.75f))
                                else Modifier
                        )
                        .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                        .padding(end = 5.dp, bottom = 2.dp)
        )
}

@Composable
internal fun TranslationPopup(
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
                                .background(LyraColorScheme.surface, RoundedCornerShape(12.dp))
                                .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(12.dp))
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
                                                color = LyraColorScheme.primary
                                        )
                                        Text(text = stringResource(Res.string.loading_dots), fontSize = 13.sp, color = LyraColorScheme.onSurfaceVariant)
                                }
                        } else {
                                Text(
                                        text = state.translationResult.translation ?: stringResource(Res.string.not_found),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = LyraColorScheme.onSurface,
                                        textAlign = TextAlign.Start
                                )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                if (state.isSingleWord) {
                                        val audio = state.audio
                                        Button(
                                                onClick = { onEvent(LyricsEvent.Audio.AudioPlayToggled) },
                                                modifier = Modifier.size(36.dp),
                                                shape = CircleShape,
                                                contentPadding = PaddingValues(0.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (audio.isPlaying) LyraColors.Correct else LyraColorScheme.primary,
                                                        contentColor = Color.White
                                                ),
                                                enabled = !audio.isLoading && !state.isTranslating
                                        ) {
                                                if (audio.isLoading) {
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
                                                isSlowMode = audio.isSlowMode,
                                                onClick = { onEvent(LyricsEvent.Audio.SlowModeToggled) },
                                                size = 30.dp,
                                        )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                OutlinedButton(
                                        onClick = { onEvent(LyricsEvent.PopupDismissed) },
                                        modifier = Modifier.height(30.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = LyraColorScheme.onSurfaceVariant
                                        )
                                ) {
                                        Text(text = stringResource(Res.string.know), fontSize = 12.sp)
                                }

                                Button(
                                        onClick = { onEvent(LyricsEvent.SaveWordRequested) },
                                        modifier = Modifier.height(30.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                                containerColor = LyraColorScheme.primary
                                        ),
                                        enabled = !state.isTranslating && state.translationResult.translation != null
                                ) {
                                        Text(text = stringResource(Res.string.learn), fontSize = 12.sp)
                                }
                        }
                }
        }
}
