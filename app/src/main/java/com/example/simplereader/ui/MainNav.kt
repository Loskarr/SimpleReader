package com.example.simplereader.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.simplereader.viewmodel.MangaViewModel


@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: MangaViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = "mangaList",
        modifier = modifier
    ) {
        composable("mangaList") {
            val uiState by viewModel.uiState.collectAsState()
            val mangaPagingData by viewModel.mangaPagingData.collectAsState()
            MangaList(
                mangaPagingData = mangaPagingData,
                uiState = uiState,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onSortChange = { viewModel.onSortChange(it) },
                onSearch = { viewModel.refresh() },
                onRefresh = { viewModel.refresh() },
                navController = navController
            )
        }
        composable("mangaDetail/{mangaId}") { backStackEntry ->
            val mangaId = backStackEntry.arguments?.getString("mangaId") ?: ""
            val mangaPagingData by viewModel.mangaPagingData.collectAsState()
            val mangaItems = mangaPagingData?.collectAsLazyPagingItems()
            val uiState by viewModel.uiState.collectAsState()
            LaunchedEffect(mangaId) {
                viewModel.loadMangaFeed(mangaId)
            }
            MangaDetailScreen(
                mangaId = mangaId,
                getMangaById = { id -> mangaItems?.itemSnapshotList?.items?.find { it.id == id } },
                uiState = uiState,
                navController = navController
            )
        }
        composable("chapterReader/{chapterId}") { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
            Log.d("ChapterReader", "Navigating to chapter: $chapterId")
            LaunchedEffect(chapterId) {
                viewModel.loadMangaChapterData(chapterId)
            }
            val uiState by viewModel.uiState.collectAsState()
            val chapters = uiState.mangaFeed?.data
            val currentIndex = chapters?.indexOfFirst { it.id == chapterId } ?: -1
            val hasPrev = chapters != null && currentIndex > 0
            val hasNext = chapters != null && currentIndex < (chapters.size - 1) && currentIndex != -1

            val currentChapter = chapters?.getOrNull(currentIndex)
            val chapterNumber = currentChapter?.attributes?.chapter
            val chapterTitle = currentChapter?.attributes?.title

            val currentMangaId = uiState.currentMangaId ?: ""

            ChapterReaderScreen(
                uiState = uiState,
                onBack = { navController.popBackStack("mangaDetail/$currentMangaId", false) },
                onPrevChapter = if (hasPrev) {
                    { navController.navigate("chapterReader/${chapters[currentIndex - 1].id}") }
                } else null,
                onNextChapter = if (hasNext) {
                    { navController.navigate("chapterReader/${chapters[currentIndex + 1].id}") }
                } else null,
                hasPrevChapter = hasPrev,
                hasNextChapter = hasNext,
                chapterNumber = chapterNumber,
                chapterTitle = chapterTitle,
            )
        }
    }
}
