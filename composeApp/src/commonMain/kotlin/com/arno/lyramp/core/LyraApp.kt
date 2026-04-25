package com.arno.lyramp.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.arno.lyramp.core.migration.AppleAuthMigration
import com.arno.lyramp.core.ui.iosBackSwipe
import com.arno.lyramp.feature.authorization.ui.AuthScreen
import com.arno.lyramp.feature.main.ui.MainScreen
import com.arno.lyramp.feature.authorization.domain.AppStartDestination
import com.arno.lyramp.feature.authorization.domain.AppStartUseCase
import org.koin.compose.koinInject

@Composable
fun LyraApp() {
        val appStartUseCase: AppStartUseCase = koinInject()
        val startDestination = remember {
                AppleAuthMigration.runIfNeeded()
                appStartUseCase()
        }

        val initialScreen = when (startDestination) {
                is AppStartDestination.ShowListeningHistory -> MainScreen
                is AppStartDestination.Authorization -> AuthScreen
        }

        Navigator(initialScreen) { nav ->
                Box(
                        modifier = Modifier.fillMaxSize().iosBackSwipe(enabled = nav.canPop) { nav.pop() }) {
                        SlideTransition(nav)
                }
        }
}