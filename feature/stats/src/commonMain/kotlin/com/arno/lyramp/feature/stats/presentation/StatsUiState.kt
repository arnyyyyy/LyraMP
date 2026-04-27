package com.arno.lyramp.feature.stats.presentation

import com.arno.lyramp.feature.stats.domain.model.LanguageStatsSnapshot

internal sealed interface StatsUiState {
        data object Loading : StatsUiState

        data class Ready(
                val snapshot: LanguageStatsSnapshot,
                val isRefreshing: Boolean = false,
        ) : StatsUiState

        data class Error(val message: String) : StatsUiState
}
