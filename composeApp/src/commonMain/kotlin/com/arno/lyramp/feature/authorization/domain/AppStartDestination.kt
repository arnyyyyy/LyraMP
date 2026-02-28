package com.arno.lyramp.feature.authorization.domain

sealed class AppStartDestination {
    object Authorization : AppStartDestination()
    object ShowListeningHistory : AppStartDestination()
}
