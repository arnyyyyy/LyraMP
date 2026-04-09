package com.arno.lyramp.core.model

enum class CefrLevel {
        A1, A2,
        B1, B2,
        C1, C2
}

enum class CefrDifficultyGroup(val levels: Set<CefrLevel>, val label: String, val emoji: String) {
        BEGINNER(setOf(CefrLevel.A1, CefrLevel.A2), "A1–A2", "🟢"),
        INTERMEDIATE(setOf(CefrLevel.B1, CefrLevel.B2), "B1–B2", "🟡"),
        ADVANCED(setOf(CefrLevel.C1, CefrLevel.C2), "C1–C2", "🔴");

        fun includesLevel(level: CefrLevel): Boolean = level in levels
}
