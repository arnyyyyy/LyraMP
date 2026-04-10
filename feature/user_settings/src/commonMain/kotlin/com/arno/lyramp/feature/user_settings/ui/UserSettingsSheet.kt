package com.arno.lyramp.feature.user_settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.user_settings.model.RecommendedWordLevel
import com.arno.lyramp.feature.user_settings.presentation.UserSettingsState
import com.arno.lyramp.feature.user_settings.resources.Res
import com.arno.lyramp.feature.user_settings.resources.settings_done
import com.arno.lyramp.feature.user_settings.resources.settings_learning_languages
import com.arno.lyramp.feature.user_settings.resources.settings_title
import com.arno.lyramp.feature.user_settings.resources.settings_word_level
import com.arno.lyramp.ui.LyraFilledButton
import com.arno.lyramp.ui.theme.LyraColorScheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSettingsSheet(
        state: UserSettingsState,
        availableLanguages: List<String>,
        onToggleLanguage: (String) -> Unit,
        onSelectLevel: (String, RecommendedWordLevel) -> Unit,
        onDone: () -> Unit,
        onDismiss: () -> Unit,
) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
                onDismissRequest = onDismiss,
                sheetState = sheetState,
                containerColor = LyraColorScheme.surface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                dragHandle = {
                        Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                contentAlignment = Alignment.Center
                        ) {
                                Box(
                                        modifier = Modifier
                                                .width(40.dp)
                                                .height(4.dp)
                                                .background(LyraColorScheme.outline, RoundedCornerShape(2.dp))
                                )
                        }
                },
        ) {
                SheetContent(
                        availableLanguages = availableLanguages,
                        selectedLanguages = state.selectedLanguages,
                        wordLevels = state.wordLevels,
                        onToggleLanguage = onToggleLanguage,
                        onSelectLevel = onSelectLevel,
                        onDone = onDone,
                )
        }
}

@Composable
private fun SheetContent(
        availableLanguages: List<String>,
        selectedLanguages: Set<String>,
        wordLevels: Map<String, RecommendedWordLevel>,
        onToggleLanguage: (String) -> Unit,
        onSelectLevel: (String, RecommendedWordLevel) -> Unit,
        onDone: () -> Unit,
) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp, bottom = 24.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                ) {
                        Text(text = "⚙️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = stringResource(Res.string.settings_title),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyraColorScheme.onSurface,
                        )
                }

                Spacer(modifier = Modifier.height(28.dp))

                SectionLabel(text = stringResource(Res.string.settings_learning_languages))

                FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                        availableLanguages.forEach { lang ->
                                LanguageChip(
                                        code = lang,
                                        isSelected = lang in selectedLanguages,
                                        onClick = { onToggleLanguage(lang) }
                                )
                        }
                }

                Spacer(modifier = Modifier.height(28.dp))

                val activeLanguages = availableLanguages.filter { it in selectedLanguages }
                if (activeLanguages.isNotEmpty()) {
                        SectionLabel(text = stringResource(Res.string.settings_word_level))

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                activeLanguages.forEach { lang ->
                                        LanguageLevelCard(
                                                language = lang,
                                                currentLevel = wordLevels[lang] ?: RecommendedWordLevel.ALL,
                                                onSelectLevel = { level -> onSelectLevel(lang, level) }
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(28.dp))
                }

                LyraFilledButton(
                        text = stringResource(Res.string.settings_done),
                        onClick = onDone,
                        modifier = Modifier.fillMaxWidth(),
                        height = 52.dp,
                )
        }
}

@Composable
private fun SectionLabel(text: String) {
        Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = LyraColorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
}
