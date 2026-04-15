package com.arno.lyramp.feature.listening_history.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.user_settings.ui.LanguageSelectorDropdown

@Composable
internal fun LanguageSelectorWithAddButtonWrapper(
        selectedLanguage: String?,
        availableLanguages: List<String>,
        onLanguageSelected: (String) -> Unit,
        onSettingsClick: () -> Unit,
        onAddContentClick: () -> Unit,
        modifier: Modifier = Modifier,
) {
        var addRevealed by remember { mutableStateOf(false) }

        Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.pointerInput(Unit) {
                        var totalDrag = 0f
                        detectHorizontalDragGestures(
                                onDragStart = { totalDrag = 0f },
                                onDragEnd = {
                                        if (totalDrag < -20) addRevealed = true
                                        else if (totalDrag > 20) addRevealed = false
                                },
                                onHorizontalDrag = { _, dragAmount -> totalDrag += dragAmount }
                        )
                },
        ) {
                AnimatedVisibility(
                        visible = addRevealed,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                ) {
                        Box(
                                modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(48.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                        .clickable {
                                                addRevealed = false
                                                onAddContentClick()
                                        },
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = "+",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                )
                        }
                }

                LanguageSelectorDropdown(
                        selectedLanguage = selectedLanguage,
                        availableLanguages = availableLanguages,
                        onLanguageSelected = onLanguageSelected,
                        onSettingsClick = onSettingsClick,
                )
        }
}
