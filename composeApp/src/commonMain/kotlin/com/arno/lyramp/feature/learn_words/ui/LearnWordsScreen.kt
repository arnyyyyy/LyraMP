package com.arno.lyramp.feature.learn_words.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.arno.lyramp.feature.onboarding.ui.background.OnboardingBackground
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsScreenModel
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsUiState
import com.arno.lyramp.feature.story_generator.ui.StoryGeneratorVoyagerScreen
import com.arno.lyramp.feature.translation.model.WordInfo
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.util.getLanguageFlag
import lyramp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

internal object LearnWordsScreen : Screen {
        @Composable
        override fun Content() {
                LearnWordsTab()
        }
}

@Composable
internal fun LearnWordsTab() {
        val screenModel: LearnWordsScreenModel = koinInject()
        val uiState by screenModel.uiState.collectAsState()
        val selectedLanguage by screenModel.selectedLanguage.collectAsState()
        val availableLanguages by screenModel.availableLanguages.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        val showBackButton = uiState is LearnWordsUiState.Cards ||
                uiState is LearnWordsUiState.Cram ||
                uiState is LearnWordsUiState.Test

        Box(modifier = Modifier.fillMaxSize()) {
                OnboardingBackground(modifier = Modifier.fillMaxSize())

                Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
                        Row(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                ) {
                                        if (showBackButton) {
                                                Box(
                                                        modifier = Modifier
                                                                .size(40.dp)
                                                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                                                .clickable { screenModel.onBackToModes() },
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Text(
                                                                text = stringResource(Res.string.words_back_arrow),
                                                                fontSize = 24.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White
                                                        )
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                        }

                                        Text(
                                                text = stringResource(Res.string.nav_words),
                                                fontSize = 36.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                        )
                                }

                                if (uiState is LearnWordsUiState.ModeSelection && availableLanguages.isNotEmpty()) {
                                        LanguageSelector(
                                                selectedLanguage = selectedLanguage,
                                                availableLanguages = availableLanguages,
                                                onLanguageSelected = { screenModel.selectLanguage(it) }
                                        )
                                }
                        }

                        Box(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp),
                                contentAlignment = when (uiState) {
                                        is LearnWordsUiState.Loading,
                                        is LearnWordsUiState.Empty,
                                        is LearnWordsUiState.ModeSelection,
                                        is LearnWordsUiState.Completed -> Alignment.Center

                                        else -> Alignment.TopCenter
                                }
                        ) {
                                when (val state = uiState) {
                                        is LearnWordsUiState.Loading -> {
                                                CircularProgressIndicator(color = Color(0xFF4A90E2))
                                        }

                                        is LearnWordsUiState.Empty -> {
                                                SavedWordsEmpty()
                                        }

                                        is LearnWordsUiState.ModeSelection -> {
                                                ModeSelectionContent(
                                                        onSelectMode = { mode -> screenModel.onSelectMode(mode) },
                                                        onNavigateToStories = { navigator.push(StoryGeneratorVoyagerScreen) }
                                                )
                                        }

                                        is LearnWordsUiState.Cards -> {
                                                CardsModeContent(
                                                        state = state,
                                                        onSwipe = { wordId, isKnown -> screenModel.onCardSwipe(wordId, isKnown) },
                                                        onToggleImportance = { wordId, isImportant -> screenModel.onToggleImportance(wordId, isImportant) },
                                                        onRequestSpeech = { savedWord ->
                                                                screenModel.getSourceSpeechFilePath(
                                                                        WordInfo(
                                                                                word = savedWord.word,
                                                                                translation = savedWord.translation,
                                                                                sourceLang = savedWord.sourceLang
                                                                        )
                                                                )
                                                        }
                                                )
                                        }

                                        is LearnWordsUiState.Cram -> {
                                                CramModeContent(
                                                        state = state,
                                                        onInputChange = { screenModel.onLearnInputChange(it) },
                                                        onCheck = { screenModel.onLearnCheck() },
                                                        onNext = { screenModel.onLearnNext() }
                                                )
                                        }

                                        is LearnWordsUiState.Test -> {
                                                TestModeContent(
                                                        state = state,
                                                        onSelectOption = { screenModel.onTestSelectOption(it) },
                                                        onNext = { screenModel.onTestNext() }
                                                )
                                        }

                                        is LearnWordsUiState.Completed -> {
                                                CompletedContent(state = state, onRestart = { screenModel.onRestart() })
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun SavedWordsEmpty() {
        Box(
                modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(0.85f)
                        .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(40.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(Res.string.words_book_icon), fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = stringResource(Res.string.words_empty_title),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = stringResource(Res.string.words_empty_subtitle),
                                fontSize = 15.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                        )
                }
        }
}

@Composable
private fun LanguageSelector(
        selectedLanguage: String?,
        availableLanguages: List<String>,
        onLanguageSelected: (String) -> Unit
) {
        var expanded by remember { mutableStateOf(false) }

        Box {
                Box(
                        modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .clickable {
                                        if (availableLanguages.size > 1) {
                                                expanded = !expanded
                                        }
                                },
                        contentAlignment = Alignment.Center
                ) {
                        Text(
                                text = getLanguageFlag(selectedLanguage ?: "en"),
                                fontSize = 24.sp
                        )
                }

                DropdownMenu(
                        expanded = expanded && availableLanguages.size > 1,
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
                                                if (lang == selectedLanguage) Color(0xFF4A90E2).copy(alpha = 0.2f)
                                                else Color.Transparent
                                        )
                                )
                        }
                }
        }
}
