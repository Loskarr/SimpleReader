package com.example.simplereader.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MangaChapter(
    val id: String,
    val type: String,
    val attributes: MangaChapterAttributes,
    val relationships: JsonElement
)

@Serializable
data class MangaChapterAttributes(
    val volume: String? = null,
    val chapter: String? = null,
    val title: String? = null,
    val translatedLanguage: String,
    val externalUrl: String? = null,
    val isUnavailable: Boolean = false,
    val publishAt: String,
    val readableAt: String,
    val createdAt: String,
    val updatedAt: String,
    val pages: Int,
    val version: Int
)