package com.arno.lyramp.feature.authorization.domain.model

sealed class AppStartDestination {
        object Authorization : AppStartDestination()
        object ShowListeningHistory : AppStartDestination()
}