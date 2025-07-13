package com.example.simplereader.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MangaFeed (
    val result: String,
    val response: String,
    val data: List<MangaChapter>,
    val limit: Int = 0,
    val offset: Int = 0,
    val total: Int = 0
)