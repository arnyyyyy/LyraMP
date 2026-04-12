package com.arno.lyramp.feature.stories_generator.model

data class StoryWord(
        val word: String,
        val translation: String,
)

data class GeneratedStory(
        val text: String,
        val wordsUsed: List<StoryWord>,
        val generationTimeMs: Long
)

enum class DownloadableModel(
        val label: String,
        val fileName: String,
        val downloadUrl: String,
        val sizeLabel: String,
        val description: String
) {
        LARGE(
                label = "Qwen3-0.6B",
                fileName = "LFM2.5-1.2B-Instruct-Q2_K.gguf",
//                downloadUrl = "https://huggingface.co/unsloth/Qwen3-0.6B-GGUF/resolve/main/Qwen3-0.6B-Q2_K.gguf?download=true",
//                downloadUrl = "https://huggingface.co/unsloth/LFM2.5-1.2B-Instruct-GGUF/resolve/main/LFM2.5-1.2B-Instruct-Q2_K.gguf?download=true",
                // https://huggingface.co/unsloth/Qwen3.5-0.8B-GGUF -- новее
                downloadUrl = "https://huggingface.co/unsloth/Qwen3-0.6B-GGUF/resolve/main/Qwen3-0.6B-Q4_K_S.gguf?download=true",
                sizeLabel = "~350 MB",
                description = "Требования:  ≥4 ГБ RAM"
        )
}

