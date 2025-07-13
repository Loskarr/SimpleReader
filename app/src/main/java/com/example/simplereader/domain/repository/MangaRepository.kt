package com.example.simplereader.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.simplereader.data.model.ChapterDataResponse
import com.example.simplereader.data.model.MangaDexManga
import com.example.simplereader.data.model.MangaFeed
import com.example.simplereader.data.network.MangaDexApiService
import com.example.simplereader.data.network.NetworkModule
import com.example.simplereader.data.paging.MangaPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MangaRepository {
    
    private val apiService: MangaDexApiService = NetworkModule.apiService

    fun getMangaPaging(
        title: String? = null,
        orderByFollows: String? = null,
        orderByLatest: String? = null,
        orderByRating: String? = null
    ): Flow<PagingData<MangaDexManga>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                MangaPagingSource(
                    apiService = apiService,
                    title = title,
                    orderByFollows = orderByFollows,
                    orderByLatest = orderByLatest,
                    orderByRating = orderByRating
                )
            }
        ).flow
    }

    fun getMangaFeed(mangaId: String): Flow<Result<MangaFeed>> = flow {
        try {
            val response = apiService.getMangaFeed(mangaId)
            if (response.isSuccessful) {
                val feed = response.body()
                if (feed != null) {
                    emit(Result.success(feed))
                } else {
                    emit(Result.failure(Exception("No feed data")))
                }
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()} - ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getMangaChapterData(chapterId: String): Flow<Result<ChapterDataResponse>> = flow {
        try {
            val response = apiService.getMangaChapterImage(chapterId)
            if (response.isSuccessful) {
                val chapterData = response.body()
                if (chapterData != null) {
                    emit(Result.success(chapterData))
                } else {
                    emit(Result.failure(Exception("No chapter data")))
                }
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()} - ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
