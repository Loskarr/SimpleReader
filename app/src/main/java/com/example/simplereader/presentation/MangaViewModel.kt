package com.example.simplereader.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplereader.data.model.ChapterDataResponse
import com.example.simplereader.data.model.MangaDexManga
import com.example.simplereader.data.model.MangaFeed
import com.example.simplereader.data.repository.MangaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

data class MangaUiState(
    val mangaList: List<MangaDexManga> = emptyList(),
    val mangaFeed: MangaFeed? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedSort: String = "Relevance",

    val chapterData: ChapterDataResponse? = null,
    val isChapterLoading: Boolean = false,
    val chapterError: String? = null,
    val currentMangaId: String? = null
)

class MangaViewModel : ViewModel() {
    private val repository = MangaRepository()

    private val _uiState = MutableStateFlow(MangaUiState())
    val uiState: StateFlow<MangaUiState> = _uiState.asStateFlow()

    private val _mangaPagingData = MutableStateFlow<Flow<PagingData<MangaDexManga>>?>(null)
    val mangaPagingData: StateFlow<Flow<PagingData<MangaDexManga>>?> = _mangaPagingData.asStateFlow()

    init {
        loadMangaPaging()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onSortChange(sort: String) {
        _uiState.value = _uiState.value.copy(selectedSort = sort)
    }

    fun loadMangaPaging() {
        val title = _uiState.value.searchQuery.takeIf { it.isNotBlank() }
        val orderByFollows = if (_uiState.value.selectedSort == "Popular") "desc" else null
        val orderByLatest = if (_uiState.value.selectedSort == "Created At") "desc" else null
        val orderByRating = if (_uiState.value.selectedSort == "Rating") "desc" else null

        _mangaPagingData.value = repository.getMangaPaging(
            title = title,
            orderByFollows = orderByFollows,
            orderByLatest = orderByLatest,
            orderByRating = orderByRating
        ).cachedIn(viewModelScope)
    }

    fun loadMangaFeed(mangaId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(currentMangaId = mangaId)
            repository.getMangaFeed(mangaId).collect { result ->
                result.fold(
                    onSuccess = { feed ->
                        android.util.Log.d("MangaFeed", "Feed: $feed")
                        _uiState.value = _uiState.value.copy(mangaFeed = feed)
                    },
                    onFailure = { exception ->
                        android.util.Log.e("MangaFeed", "Error: ${exception.message}")
                        _uiState.value = _uiState.value.copy(mangaFeed = null)
                    }
                )
            }
        }
    }


    fun loadMangaChapterData(chapterId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isChapterLoading = true, chapterError = null)
            repository.getMangaChapterData(chapterId).collect { result ->
                result.fold(
                    onSuccess = { chapterData ->
                        Log.d("MangaChapterData", "Chapter Data: $chapterData")
                        _uiState.value = _uiState.value.copy(
                            chapterData = chapterData,
                            isChapterLoading = false,
                            chapterError = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            chapterData = null,
                            isChapterLoading = false,
                            chapterError = exception.message
                        )
                    }
                )
            }
        }
    }

    fun refresh() {
        loadMangaPaging()
    }

}
