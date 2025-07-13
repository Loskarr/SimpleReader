package com.example.simplereader.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexAttributes(
    val title: Map<String, String>,
    val altTitles: List<Map<String, String>> = emptyList(),
    val description: Map<String, String> = emptyMap(),
    val isLocked: Boolean = false,
    val links: Map<String, String> = emptyMap(),
    val originalLanguage: String = "",
    val lastVolume: String? = null,
    val lastChapter: String? = null,
    val publicationDemographic: String? = null,
    val status: String = "",
    val year: Int? = null,
    val contentRating: String = "",
    val tags: List<MangaDexTag> = emptyList(),
    val state: String = "",
    val chapterNumbersResetOnNewVolume: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = "",
    val version: Int = 1,
    val availableTranslatedLanguages: List<String> = emptyList(),
    val latestUploadedChapter: String? = null,
    val fileName: String? = null,
    val locale: String? = null,
    val volume: String? = null
)
