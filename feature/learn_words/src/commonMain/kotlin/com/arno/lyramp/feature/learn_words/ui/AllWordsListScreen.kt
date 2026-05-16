package com.arno.lyramp.feature.learn_words.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.learn_words.presentation.AllWordsListScreenModel
import com.arno.lyramp.feature.learn_words.presentation.WordInfo
import com.arno.lyramp.feature.learn_words.resources.Res
import com.arno.lyramp.feature.learn_words.resources.all_words_count
import com.arno.lyramp.feature.learn_words.resources.all_words_empty_subtitle
import com.arno.lyramp.feature.learn_words.resources.all_words_empty_title
import com.arno.lyramp.feature.learn_words.resources.all_words_screen_title
import com.arno.lyramp.feature.learn_words.resources.audio_play
import com.arno.lyramp.feature.learn_words.resources.cefr_advanced
import com.arno.lyramp.feature.learn_words.resources.cefr_beginner
import com.arno.lyramp.feature.learn_words.resources.cefr_intermediate
import com.arno.lyramp.feature.learn_words.resources.filter_all
import com.arno.lyramp.feature.learn_words.resources.filter_important
import com.arno.lyramp.feature.learn_words.resources.important_toggle
import com.arno.lyramp.feature.learn_words.resources.words_empty_title
import com.arno.lyramp.feature.learn_words.resources.words_loading
import com.arno.lyramp.ui.LocalNavBarHeight
import com.arno.lyramp.ui.VocabularyWordItem
import com.arno.lyramp.ui.VocabularyWordsContent
import com.arno.lyramp.ui.VocabularyWordsText
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class AllWordsListScreen(
        private val language: String?,
) : Screen {

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val screenModel = getScreenModel<AllWordsListScreenModel> { parametersOf(language) }
                val state by screenModel.uiState.collectAsState()

                DisposableEffect(Unit) {
                        onDispose { screenModel.onDispose() }
                }

                val wordsById = state.allWords.associateBy { it.id }
                VocabularyWordsContent(
                        texts = VocabularyWordsText(
                                title = stringResource(Res.string.all_words_screen_title),
                                count = if (state.allWords.isEmpty()) null else stringResource(Res.string.all_words_count, state.allWords.size),
                                loading = stringResource(Res.string.words_loading),
                                emptyTitle = stringResource(Res.string.all_words_empty_title),
                                emptySubtitle = stringResource(Res.string.all_words_empty_subtitle),
                                noFilteredWordsTitle = stringResource(Res.string.words_empty_title),
                                filterAll = stringResource(Res.string.filter_all),
                                filterImportant = stringResource(Res.string.filter_important),
                                cefrBeginner = stringResource(Res.string.cefr_beginner),
                                cefrIntermediate = stringResource(Res.string.cefr_intermediate),
                                cefrAdvanced = stringResource(Res.string.cefr_advanced),
                                audioPlay = stringResource(Res.string.audio_play),
                                importantToggle = stringResource(Res.string.important_toggle),
                        ),
                        allWords = state.allWords.map { it.toVocabularyItem() },
                        visibleWords = state.visibleWords.map { it.toVocabularyItem() },
                        filter = state.filter,
                        availableCefrGroups = state.availableCefrGroups,
                        cefrByWord = state.cefrByWord,
                        isLoading = state.isLoading,
                        loadingAudioWordId = state.loadingAudioWordId,
                        playingAudioWordId = state.playingAudioWordId,
                        onBack = { navigator.pop() },
                        onSelectFilter = screenModel::selectFilter,
                        onToggleImportant = screenModel::toggleImportant,
                        onSpeakWord = { item -> wordsById[item.id]?.let(screenModel::speakWord) },
                        modifier = Modifier,
                        bottomPadding = LocalNavBarHeight.current,
                )
        }
}

private fun WordInfo.toVocabularyItem() = VocabularyWordItem(
        id = id,
        word = word,
        translation = translation,
        isImportant = isImportant,
)
