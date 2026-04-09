package com.arno.lyramp.feature.user_settings.model

import com.arno.lyramp.core.model.CefrLevel

enum class RecommendedWordLevel(
        val levels: Set<CefrLevel>,
        val label: String,
        val emoji: String,
) {
        BEGINNER(
                levels = setOf(CefrLevel.A1, CefrLevel.A2),
                label = "A1–A2",
                emoji = "🟢"
        ),
        INTERMEDIATE(
                levels = setOf(CefrLevel.B1, CefrLevel.B2),
                label = "B1–B2",
                emoji = "🟡"
        ),
        UPPER(
                levels = setOf(CefrLevel.B2, CefrLevel.C1, CefrLevel.C2),
                label = "B2+",
                emoji = "🟠"
        ),
        ALL(
                levels = CefrLevel.entries.toSet(),
                label = "Все слова",
                emoji = "📚"
        );
}

