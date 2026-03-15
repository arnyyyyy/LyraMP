package com.arno.lyramp.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.arno.lyramp.feature.learn_words.ui.LearnWordsScreen

object WordsTab : Tab {

        var navigator: Navigator? = null
                private set

        val scrollToTopToken: MutableState<Int> = mutableIntStateOf(0)

        override val options: TabOptions
                @Composable
                get() = remember {
                        TabOptions(index = 1u, title = "Слова", icon = null)
                }

        @Composable
        override fun Content() {
                Navigator(LearnWordsScreen) { nav ->
                        navigator = nav
                        SlideTransition(nav)
                }
        }
}
