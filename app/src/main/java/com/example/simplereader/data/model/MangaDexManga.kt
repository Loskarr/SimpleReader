package com.example.simplereader.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexManga(
    val id: String,
    val type: String,
    val attributes: MangaDexAttributes,
    val relationships: List<MangaDexRelationship>
) {
    fun getCoverArt(): MangaDexRelationship? {
        return relationships.find { it.type == "cover_art" }
    }
    
    fun getTitle(): String {
        return attributes.title["en"] 
            ?: attributes.title.values.firstOrNull() 
            ?: "Unknown Title"
    }
    
    fun getDescription(): String {
        return attributes.description["en"]
            ?: attributes.description.values.firstOrNull()
            ?: ""
    }
}
