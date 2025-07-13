package com.example.simplereader.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexTagAttributes(
    val name: Map<String, String>,
    val description: Map<String, String> = emptyMap(),
    val group: String,
    val version: Int
)