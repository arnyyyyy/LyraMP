package com.arno.lyramp.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.arno.lyramp.feature.listening_history.ui.ShowListeningHistoryScreen

object LibraryTab : Tab {
        var navigator: Navigator? = null
                private set

        val scrollToTopToken: MutableState<Int>
                get() = ShowListeningHistoryScreen.scrollToTopToken

        override val options: TabOptions
                @Composable
                get() = remember {
                        TabOptions(index = 0u, title = "Медиатека", icon = null)
                }

        @Composable
        override fun Content() {
                Navigator(ShowListeningHistoryScreen) { nav ->
                        navigator = nav
                        SlideTransition(nav)
                }
        }
}
