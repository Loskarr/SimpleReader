package com.example.simplereader.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexRelationship(
    val id: String,
    val type: String,
    val attributes: MangaDexRelationshipAttributes? = null
) {
    fun getCoverUrl(mangaId: String): String? {
        return if (type == "cover_art" && attributes?.fileName != null) {
            "https://uploads.mangadex.org/covers/$mangaId/${attributes.fileName}"
        } else null
    }
}
