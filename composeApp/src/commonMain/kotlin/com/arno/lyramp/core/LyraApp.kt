package com.arno.lyramp.core

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.arno.lyramp.feature.authorization.ui.AuthorizationScreen
import com.arno.lyramp.feature.listening_history.ui.ShowListeningHistoryScreen
import com.arno.lyramp.feature.authorization.domain.usecase.AppStartDestination
import com.arno.lyramp.feature.authorization.domain.usecase.AppStartUseCase
import org.koin.compose.koinInject

@Composable
fun LyraApp() {
        val appStartUseCase: AppStartUseCase = koinInject()
        val startDestination = appStartUseCase()

        val initialScreen = when (startDestination) {
                is AppStartDestination.ShowListeningHistory -> ShowListeningHistoryScreen
                is AppStartDestination.Authorization -> AuthorizationScreen
        }

        Navigator(initialScreen) { nav ->
                SlideTransition(nav)
        }
}