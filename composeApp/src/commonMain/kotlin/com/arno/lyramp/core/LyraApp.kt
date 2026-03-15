package com.arno.lyramp.core

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.arno.lyramp.feature.authorization.ui.AuthorizationScreen
import com.arno.lyramp.feature.main.ui.MainScreen
import com.arno.lyramp.feature.authorization.domain.AppStartDestination
import com.arno.lyramp.feature.authorization.domain.AppStartUseCase
import org.koin.compose.koinInject

@Composable
fun LyraApp() {
        val appStartUseCase: AppStartUseCase = koinInject()
        val startDestination = appStartUseCase()

        val initialScreen = when (startDestination) {
                is AppStartDestination.ShowListeningHistory -> MainScreen
                is AppStartDestination.Authorization -> AuthorizationScreen
        }

        Navigator(initialScreen) { nav ->
                SlideTransition(nav)
        }
}