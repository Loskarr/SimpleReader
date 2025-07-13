package com.example.simplereader.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexRelationshipAttributes(
    val fileName: String? = null,
    val locale: String? = null,
    val volume: String? = null
)
