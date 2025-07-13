package com.example.simplereader.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexTag(
    val id: String,
    val type: String,
    val attributes: MangaDexTagAttributes
)


