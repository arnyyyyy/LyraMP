package com.arno.lyramp.feature.user_settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.util.getLanguageFlag

@Composable
fun LanguageSelectorDropdown(
        selectedLanguage: String?,
        availableLanguages: List<String>,
        onLanguageSelected: (String) -> Unit,
        onSettingsClick: () -> Unit,
        modifier: Modifier = Modifier,
) {
        var expanded by remember { mutableStateOf(false) }
        val displayLanguage = selectedLanguage ?: availableLanguages.firstOrNull()

        Box(modifier = modifier) {
                Box(
                        modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .clickable {
                                        expanded = !expanded
                                },
                        contentAlignment = Alignment.Center
                ) {
                        Text(
                                text = getLanguageFlag(displayLanguage ?: "en"),
                                fontSize = 24.sp
                        )
                }

                DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                ) {
                        availableLanguages.forEach { lang ->
                                DropdownMenuItem(
                                        text = {
                                                Text(
                                                        text = getLanguageFlag(lang),
                                                        fontSize = 22.sp
                                                )
                                        },
                                        onClick = {
                                                onLanguageSelected(lang)
                                                expanded = false
                                        },
                                        modifier = Modifier.background(
                                                if (lang == displayLanguage) LyraColorScheme.primary.copy(alpha = 0.2f)
                                                else Color.Transparent
                                        )
                                )
                        }

                        HorizontalDivider(
                                color = LyraColorScheme.outline,
                                modifier = Modifier.padding(vertical = 4.dp)
                        )

                        DropdownMenuItem(
                                text = { Text(text = "🧑‍🎓", fontSize = 22.sp) },// TODO:  более универсальная иконка?
                                onClick = {
                                        expanded = false
                                        onSettingsClick()
                                }
                        )
                }
        }
}
