package com.arno.lyramp.feature.authorization.domain.usecase

sealed class AppStartDestination {
    object Authorization : AppStartDestination()
    object ShowListeningHistory : AppStartDestination()
}

