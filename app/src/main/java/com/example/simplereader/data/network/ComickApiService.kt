package com.example.simplereader.data.network

import com.example.simplereader.data.model.ChapterData
import com.example.simplereader.data.model.ChapterDataResponse
import com.example.simplereader.data.model.MangaDexResponse
import com.example.simplereader.data.model.MangaFeed
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MangaDexApiService {
    @GET("manga")
    suspend fun getManga(
        @Query("title") title: String? = null,
        @Query("order[followedCount]") orderByFollows: String? = null,
        @Query("order[createdAt]") orderByLatest: String? = null,
        @Query("order[rating]") orderByRating: String? = null,
        @Query("limit") limit: Int = 24,
        @Query("offset") offset: Int = 0,
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
        @Query("contentRating[]") contentRating: List<String> = listOf("safe")
    ): Response<MangaDexResponse>
    
    @GET("manga/{id}/feed")
    suspend fun getMangaFeed(
        @Path("id") mangaId: String,
        @Query("translatedLanguage[]") language: List<String> = listOf("en"),
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("order[chapter]") orderByChapter: String = "asc",
        @Query("translatedLanguage[]") translatedLanguage: List<String> = listOf("en")
    ): Response<MangaFeed>

    @GET("at-home/server/{id}")
    suspend fun getMangaChapterImage(
        @Path("id") chapterId: String
    ): Response<ChapterDataResponse>

    companion object {
        const val BASE_URL = "https://api.mangadex.org/"
        const val COVER_BASE_URL = "https://uploads.mangadex.org/covers/"
    }
}
