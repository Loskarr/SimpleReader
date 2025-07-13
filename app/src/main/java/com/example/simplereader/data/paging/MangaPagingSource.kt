package com.example.simplereader.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplereader.data.model.MangaDexManga
import com.example.simplereader.data.network.MangaDexApiService

class MangaPagingSource(
    private val apiService: MangaDexApiService,
    private val title: String?,
    private val orderByFollows: String?,
    private val orderByLatest: String?,
    private val orderByRating: String?
) : PagingSource<Int, MangaDexManga>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MangaDexManga> {
        return try {
            val offset = params.key ?: 0
            val limit = params.loadSize

            val response = apiService.getManga(
                title = title,
                orderByFollows = orderByFollows,
                orderByLatest = orderByLatest,
                orderByRating = orderByRating,
                limit = limit,
                offset = offset
            )

            if (response.isSuccessful) {
                val mangaResponse = response.body()
                if (mangaResponse != null && mangaResponse.result == "ok") {
                    LoadResult.Page(
                        data = mangaResponse.data,
                        prevKey = if (offset == 0) null else offset - limit,
                        nextKey = if (mangaResponse.data.isEmpty()) null else offset + limit
                    )
                } else {
                    LoadResult.Error(Exception("API returned error result"))
                }
            } else {
                LoadResult.Error(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MangaDexManga>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(state.config.pageSize) ?: anchorPage?.nextKey?.minus(state.config.pageSize)
        }
    }
}
