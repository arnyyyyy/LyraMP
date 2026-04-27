package com.arno.lyramp.feature.stats.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.stats.domain.usecase.GetLanguageStatsUseCase
import com.arno.lyramp.feature.stats.domain.usecase.ProcessTracksCefrUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.ObserveSelectedLanguageUseCase
import com.arno.lyramp.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class StatsScreenModel(
        private val languageOverride: String?,
        observeSelectedLanguage: ObserveSelectedLanguageUseCase,
        private val getLanguageStats: GetLanguageStatsUseCase,
        private val processTracks: ProcessTracksCefrUseCase,
) : ScreenModel {
        private val selectedLanguageFlow = observeSelectedLanguage()

        private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
        val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

        init {
                screenModelScope.launch {
                        loadStatsInternal()
                }
        }

        fun retryLoad() = screenModelScope.launch { loadStatsInternal() }

        fun refresh() {
                screenModelScope.launch {
                        val current = _uiState.value
                        if (current is StatsUiState.Ready) {
                                _uiState.value = current.copy(isRefreshing = true)
                        }
                        try {
                                processTracks()
                                loadStatsInternal()
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (e: Exception) {
                                Log.logger.e(e) { "Stats refresh failed" }
                                val now = _uiState.value
                                if (now is StatsUiState.Ready) {
                                        _uiState.value = now.copy(isRefreshing = false)
                                }
                        }
                }
        }

        private suspend fun loadStatsInternal() {
                try {
                        val lang = languageOverride ?: selectedLanguageFlow.first() ?: "en"
                        val snapshot = getLanguageStats(lang)
                        _uiState.value = StatsUiState.Ready(snapshot = snapshot, isRefreshing = false)
                } catch (ce: CancellationException) {
                        throw ce
                } catch (e: Exception) {
                        Log.logger.e(e) { "Stats load failed" }
                        _uiState.value = StatsUiState.Error(message = e.message ?: "Unknown error")
                }
        }
}
