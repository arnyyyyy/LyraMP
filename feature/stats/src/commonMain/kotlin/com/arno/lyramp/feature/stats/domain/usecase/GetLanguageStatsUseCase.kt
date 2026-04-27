package com.arno.lyramp.feature.stats.domain.usecase

import com.arno.lyramp.core.model.CefrDifficultyGroup
import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.feature.extraction.domain.usecase.GetShownWordsUseCase
import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllLearnWordsUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetRecentTracksUseCase
import com.arno.lyramp.feature.stats.data.StatsTrackCefrWordRepository
import com.arno.lyramp.feature.stats.data.StatsTrackMetaRepository
import com.arno.lyramp.feature.stats.domain.model.CefrGroupStats
import com.arno.lyramp.feature.stats.domain.model.LanguageStatsSnapshot
import com.arno.lyramp.feature.stats.domain.model.TrackCefrWord
import kotlinx.coroutines.flow.first

internal class GetLanguageStatsUseCase(
        private val getAllLearnWords: GetAllLearnWordsUseCase,
        private val getShownWords: GetShownWordsUseCase,
        private val getRecentTracks: GetRecentTracksUseCase,
        private val cefrWordRepository: StatsTrackCefrWordRepository,
        private val metaRepository: StatsTrackMetaRepository,
) {
        suspend operator fun invoke(language: String): LanguageStatsSnapshot {
                val learnWords = getAllLearnWords().first().filter { it.sourceLang == language }
                val learnWordsByLower = learnWords.associateBy { it.word.lowercase() }

                val learnEdWords = learnWords.filter { it.isLearned() }
                val learnIngWords = learnWords - learnEdWords

                val shownWords = getShownWords.forStatsLanguage(language)

                val knownSet = buildSet {
                        learnEdWords.forEach { add(it.word.lowercase()) }
                        shownWords.forEach { shown ->
                                val learn = learnWordsByLower[shown]
                                if (learn == null || learn.isLearned()) add(shown)
                        }
                }

                val cefrRows = cefrWordRepository.getForLanguage(language)
                val groupStats = aggregateByGroup(cefrRows, knownSet)

                val metas = metaRepository.getForLanguage(language)
                val rowsByTrack = cefrRows.groupBy { it.trackId }
                val fullyLearnedTracks = metas.count { meta ->
                        val rows = rowsByTrack[meta.trackId].orEmpty()
                        rows.isNotEmpty() && rows.all { it.word.lowercase() in knownSet }
                }

                val libraryTracksCount = runCatching {
                        getRecentTracks().count { it.language == language }
                }.getOrDefault(0)

                return LanguageStatsSnapshot(
                        language = language,
                        learningWordsCount = learnIngWords.size,
                        learnedWordsCount = learnEdWords.size,
                        libraryTracksCount = libraryTracksCount,
                        processedTracksCount = metas.size,
                        fullyLearnedTracksCount = fullyLearnedTracks,
                        groupStats = groupStats,
                )
        }

        private fun aggregateByGroup(
                rows: List<TrackCefrWord>,
                knownSet: Set<String>,
        ): List<CefrGroupStats> {
                val distinctWordsByLevel = rows
                        .groupBy(
                                keySelector = { runCatching { CefrLevel.valueOf(it.cefrLevel) }.getOrNull() },
                                valueTransform = { it.word.lowercase() },
                        )
                        .filterKeys { it != null }
                        .mapKeys { (key, _) -> key!! }
                        .mapValues { (_, words) -> words.toSet() }

                return CefrDifficultyGroup.entries.map { group ->
                        val wordsInGroup = buildSet {
                                group.levels.forEach { level ->
                                        distinctWordsByLevel[level]?.let { addAll(it) }
                                }
                        }
                        val known = wordsInGroup.count { it in knownSet }
                        CefrGroupStats(group = group, known = known, total = wordsInGroup.size)
                }
        }

        private fun LearnWordEntity.isLearned() = isKnown || progress >= 1f
}
