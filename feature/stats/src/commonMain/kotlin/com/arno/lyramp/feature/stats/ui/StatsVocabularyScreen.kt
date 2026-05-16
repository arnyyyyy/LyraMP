package com.arno.lyramp.feature.stats.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.stats.presentation.StatsVocabularyScreenModel
import com.arno.lyramp.feature.stats.presentation.StatsVocabularyStatus
import com.arno.lyramp.feature.stats.resources.Res
import com.arno.lyramp.feature.stats.resources.stats_card_learned
import com.arno.lyramp.feature.stats.resources.stats_card_learning
import com.arno.lyramp.feature.stats.resources.stats_cefr_advanced
import com.arno.lyramp.feature.stats.resources.stats_cefr_beginner
import com.arno.lyramp.feature.stats.resources.stats_cefr_intermediate
import com.arno.lyramp.feature.stats.resources.stats_filter_all
import com.arno.lyramp.feature.stats.resources.stats_filter_important
import com.arno.lyramp.feature.stats.resources.stats_words_count
import com.arno.lyramp.feature.stats.resources.stats_words_empty_subtitle
import com.arno.lyramp.feature.stats.resources.stats_words_empty_title
import com.arno.lyramp.feature.stats.resources.stats_words_important_toggle
import com.arno.lyramp.feature.stats.resources.stats_words_loading
import com.arno.lyramp.feature.stats.resources.stats_words_no_filtered_title
import com.arno.lyramp.ui.LocalNavBarHeight
import com.arno.lyramp.ui.VocabularyWordsContent
import com.arno.lyramp.ui.VocabularyWordsText
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class StatsVocabularyScreen(
        private val language: String,
        private val statusName: String,
) : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val screenModel = getScreenModel<StatsVocabularyScreenModel> { parametersOf(language, statusName) }
                val state by screenModel.uiState.collectAsState()
                val navBarHeight = LocalNavBarHeight.current

                val title = when (screenModel.status) {
                        StatsVocabularyStatus.LEARNING -> stringResource(Res.string.stats_card_learning)
                        StatsVocabularyStatus.LEARNED -> stringResource(Res.string.stats_card_learned)
                }

                VocabularyWordsContent(
                        onToggleImportant = screenModel::toggleImportant,
                        ///                    ...
                        texts = VocabularyWordsText(
                                title = title,
                                count = if (state.allWords.isEmpty()) null else stringResource(Res.string.stats_words_count, state.allWords.size),
                                loading = stringResource(Res.string.stats_words_loading),
                                emptyTitle = stringResource(Res.string.stats_words_empty_title),
                                emptySubtitle = stringResource(Res.string.stats_words_empty_subtitle),
                                noFilteredWordsTitle = stringResource(Res.string.stats_words_no_filtered_title),
                                filterAll = stringResource(Res.string.stats_filter_all),
                                filterImportant = stringResource(Res.string.stats_filter_important),
                                cefrBeginner = stringResource(Res.string.stats_cefr_beginner),
                                cefrIntermediate = stringResource(Res.string.stats_cefr_intermediate),
                                cefrAdvanced = stringResource(Res.string.stats_cefr_advanced),
                                importantToggle = stringResource(Res.string.stats_words_important_toggle),
                        ),
                        allWords = state.allWords,
                        visibleWords = state.visibleWords,
                        filter = state.filter,
                        availableCefrGroups = state.availableCefrGroups,
                        cefrByWord = state.cefrByWord,
                        isLoading = state.isLoading,
                        onBack = { navigator.pop() },
                        onSelectFilter = screenModel::selectFilter,
                        bottomPadding = navBarHeight,
                )
        }
}
