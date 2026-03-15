package com.arno.lyramp.feature.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator

internal object MainScreen : Screen {

        @Composable
        override fun Content() {
                TabNavigator(
                        tab = LibraryTab,
                        tabDisposable = {
                                TabDisposable(
                                        navigator = it,
                                        tabs = listOf(LibraryTab, WordsTab)
                                )
                        }
                ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                                CurrentTab()

                                NavBar(
                                        modifier = Modifier.align(Alignment.BottomCenter)
                                )
                        }
                }
        }
}

private data class NavTabData(val tab: Tab, val icon: String)

private fun tabNavigatorFor(tab: Tab): Pair<Navigator?, MutableState<Int>?> =
        when (tab) {
                is LibraryTab -> Pair(LibraryTab.navigator, LibraryTab.scrollToTopToken)
                is WordsTab -> Pair(WordsTab.navigator, WordsTab.scrollToTopToken)
                else -> Pair(null, null)
        }

@Composable
private fun NavBar(modifier: Modifier = Modifier) {
        val tabNavigator = LocalTabNavigator.current
        val borderColor = Color(0xFFE0E0E0)

        val tabs = listOf(
                NavTabData(LibraryTab, "🎵"),
                NavTabData(WordsTab, "📚")
        )

        Box(
                modifier = modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9F9F9).copy(alpha = 0.94f))
                        .drawBehind {
                                drawLine(
                                        color = borderColor,
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, 0f),
                                        strokeWidth = 0.5.dp.toPx()
                                )
                        }
                        .navigationBarsPadding()
        ) {
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                        tabs.forEach { tabData ->
                                NavItem(
                                        tab = tabData.tab,
                                        icon = tabData.icon,
                                        tabNavigator = tabNavigator
                                )
                        }
                }
        }
}

@Composable
private fun NavItem(
        tab: Tab,
        icon: String,
        tabNavigator: TabNavigator
) {
        val selected = tabNavigator.current == tab
        val activeColor = Color(0xFFFC3C44) // TODO
        val inactiveColor = Color(0xFF8E8E93) // TODO

        Column(
                modifier = Modifier
                        .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                        ) {
                                if (tabNavigator.current != tab) {
                                        tabNavigator.current = tab
                                } else {
                                        val (tabNav, scrollToken) = tabNavigatorFor(tab)
                                        if (tabNav != null && tabNav.size > 1) {
                                                tabNav.popUntilRoot()
                                        } else if (scrollToken != null) {
                                                scrollToken.value = scrollToken.value + 1
                                        }
                                }
                        }
                        .width(80.dp)
                        .padding(top = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
                Box(
                        modifier = Modifier.size(if (selected) 28.dp else 26.dp),
                        contentAlignment = Alignment.Center
                ) {
                        Text(
                                text = icon,
                                fontSize = if (selected) 22.sp else 20.sp
                        )
                }
                Text(
                        text = tab.options.title,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected) activeColor else inactiveColor,
                        lineHeight = 12.sp,
                        letterSpacing = (-0.1).sp
                )
        }
}
