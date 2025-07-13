package com.example.simplereader.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexResponse(
    val result: String,
    val response: String,
    val data: List<MangaDexManga>,
    val limit: Int,
    val offset: Int,
    val total: Int
)
