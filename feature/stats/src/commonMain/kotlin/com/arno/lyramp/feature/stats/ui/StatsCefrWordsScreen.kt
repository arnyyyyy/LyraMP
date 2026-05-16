package com.arno.lyramp.feature.stats.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.stats.presentation.StatsCefrWordsScreenModel
import com.arno.lyramp.feature.stats.presentation.StatsCefrWordsUiState
import com.arno.lyramp.feature.stats.resources.Res
import com.arno.lyramp.feature.stats.resources.stats_cefr_words_count
import com.arno.lyramp.feature.stats.resources.stats_cefr_words_empty
import com.arno.lyramp.feature.stats.resources.stats_cefr_words_mark_shown
import com.arno.lyramp.feature.stats.resources.stats_deselect_all
import com.arno.lyramp.feature.stats.resources.stats_error_title
import com.arno.lyramp.feature.stats.resources.stats_loading
import com.arno.lyramp.feature.stats.resources.stats_select_all
import com.arno.lyramp.ui.BackButton
import com.arno.lyramp.ui.EmptyStateCard
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.OnboardingBackground
import com.arno.lyramp.ui.WordSelectionList
import com.arno.lyramp.ui.theme.LyraColors
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class StatsCefrWordsScreen(
        private val language: String,
        private val groupName: String,
) : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val screenModel = getScreenModel<StatsCefrWordsScreenModel> { parametersOf(language, groupName) }
                val state by screenModel.uiState.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                        OnboardingBackground(modifier = Modifier.fillMaxSize())
                        Column(
                                modifier = Modifier.fillMaxSize()
                                        .statusBarsPadding()
                                        .navigationBarsPadding()
                                        .padding(horizontal = 20.dp),
                        ) {
                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                        BackButton(onClick = { navigator.pop() })
                                        Text(
                                                text = (state as? StatsCefrWordsUiState.Ready)?.group?.label ?: groupName,
                                                color = Color.White,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                        )
                                }

                                when (val s = state) {
                                        StatsCefrWordsUiState.Loading -> Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center,
                                        ) {
                                                LoadingCard(message = stringResource(Res.string.stats_loading))
                                        }

                                        is StatsCefrWordsUiState.Error -> Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center,
                                        ) {
                                                ErrorCard(message = "${stringResource(Res.string.stats_error_title)}\n\n${s.message}")
                                        }

                                        is StatsCefrWordsUiState.Ready -> {
                                                if (s.words.isEmpty()) {
                                                        Box(
                                                                modifier = Modifier.fillMaxSize(),
                                                                contentAlignment = Alignment.Center,
                                                        ) {
                                                                EmptyStateCard(
                                                                        icon = "📖",
                                                                        title = stringResource(Res.string.stats_cefr_words_empty),
                                                                )
                                                        }
                                                } else {
                                                        WordSelectionList(
                                                                words = s.words,
                                                                selectedWords = s.selectedWords,
                                                                onToggleWord = screenModel::toggleWord,
                                                                onSave = { screenModel.save { navigator.pop() } },
                                                                saveButtonText = stringResource(
                                                                        Res.string.stats_cefr_words_mark_shown,
                                                                        s.selectedWords.size,
                                                                ),
                                                                saveEnabled = !s.isSaving,
                                                                headerContent = {
                                                                        Row(
                                                                                modifier = Modifier.fillMaxWidth(),
                                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                                verticalAlignment = Alignment.CenterVertically,
                                                                        ) {
                                                                                Text(
                                                                                        text = if (s.selectedWords.size == s.words.size)
                                                                                                stringResource(Res.string.stats_deselect_all)
                                                                                        else
                                                                                                stringResource(Res.string.stats_select_all),
                                                                                        fontSize = 13.sp,
                                                                                        color = LyraColors.Correct.copy(alpha = 0.8f),
                                                                                        modifier = Modifier.clickable { screenModel.toggleSelectAll() },
                                                                                )

                                                                                Text(
                                                                                        text = stringResource(Res.string.stats_cefr_words_count, s.words.size),
                                                                                        fontSize = 13.sp,
                                                                                        color = Color.White.copy(alpha = 0.5f),
                                                                                )
                                                                        }
                                                                        Spacer(modifier = Modifier.height(8.dp))
                                                                },
                                                                modifier = Modifier.fillMaxSize(),
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}
