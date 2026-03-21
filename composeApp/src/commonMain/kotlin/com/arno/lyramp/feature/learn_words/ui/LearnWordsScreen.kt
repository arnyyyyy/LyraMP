package com.arno.lyramp.feature.learn_words.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.arno.lyramp.ui.EmptyStateCard
import com.arno.lyramp.feature.onboarding.ui.background.OnboardingBackground
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsScreenModel
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsUiState
import com.arno.lyramp.feature.story_generator.ui.StoryGeneratorVoyagerScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.util.getLanguageFlag
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.nav_words
import lyramp.composeapp.generated.resources.notebook_icon
import lyramp.composeapp.generated.resources.words_empty_subtitle
import lyramp.composeapp.generated.resources.words_empty_title
import lyramp.composeapp.generated.resources.words_loading
import org.jetbrains.compose.resources.stringResource

internal object LearnWordsScreen : Screen {
        @Composable
        override fun Content() {
                val screenModel = getScreenModel<LearnWordsScreenModel>()

                val uiState by screenModel.uiState.collectAsState()

                val selectedLanguage by screenModel.selectedLanguage.collectAsState()
                val availableLanguages by screenModel.availableLanguages.collectAsState()
                val navigator = LocalNavigator.currentOrThrow

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
                                        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
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
                                                        LoadingCard(message = stringResource(Res.string.words_loading))
                                                }

                                                is LearnWordsUiState.Empty -> {
                                                        EmptyStateCard(
                                                                icon = stringResource(Res.string.notebook_icon),
                                                                title = stringResource(Res.string.words_empty_title),
                                                                subtitle = stringResource(Res.string.words_empty_subtitle),
                                                        )
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
                                                                isLoadingAudio = state.isLoadingAudio,
                                                                onPlayAudio = { wordInfo -> screenModel.playAudio(wordInfo) }
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
                                                if (lang == selectedLanguage) LyraColorScheme.primary.copy(alpha = 0.2f)
                                                else Color.Transparent
                                        )
                                )
                        }
                }
        }
}
