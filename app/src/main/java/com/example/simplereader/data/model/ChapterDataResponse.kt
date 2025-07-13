package com.example.simplereader.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChapterDataResponse(
    val result: String,
    val baseUrl: String,
    val chapter: ChapterData
)

@Serializable
data class ChapterData(
    val hash: String,
    val data: List<String>,
    val dataSaver: List<String>
)