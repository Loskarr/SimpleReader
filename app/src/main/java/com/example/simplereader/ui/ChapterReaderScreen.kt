package com.example.simplereader.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.simplereader.viewmodel.MangaUiState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterReaderScreen(
    uiState: MangaUiState,
    onBack: () -> Unit = {},
    onPrevChapter: (() -> Unit)? = null,
    onNextChapter: (() -> Unit)? = null,
    hasPrevChapter: Boolean = true,
    hasNextChapter: Boolean = true,
    chapterNumber: String? = null,
    chapterTitle: String? = null,
) {
    var isSystemBarVisible by remember { mutableStateOf(true) }
    SetSystemBarsVisible(isSystemBarVisible)

    val listState = rememberLazyListState()
    val currentPage = listState.firstVisibleItemIndex + 1
    val totalPages = uiState.chapterData?.chapter?.dataSaver?.size ?: 1
    

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = isSystemBarVisible,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                TopAppBar(
                    windowInsets = WindowInsets(0),
                    title = {
                        Text(
                            text = buildString {
                                append("Chapter ${chapterNumber ?: "?"}")
                                if (!chapterTitle.isNullOrBlank()) {
                                    append(" - $chapterTitle")
                                }
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isSystemBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomAppBar(
                    windowInsets = WindowInsets(0)
                ) {
                    val disabledAlpha = 0.4f
                    IconButton(
                        onClick = { onPrevChapter?.invoke() },
                        enabled = hasPrevChapter,
                        modifier = Modifier.graphicsLayer {
                            alpha = if (hasPrevChapter) 1f else disabledAlpha
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Chapter",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(4f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$currentPage / $totalPages",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        LinearProgressIndicator(
                            progress = { currentPage / totalPages.toFloat() },
                            modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .height(6.dp),
                            color = ProgressIndicatorDefaults.linearColor,
                            trackColor = ProgressIndicatorDefaults.linearTrackColor,
                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        )

                    }

                    IconButton(
                        onClick = { onNextChapter?.invoke() },
                        enabled = hasNextChapter,
                        modifier = Modifier.graphicsLayer {
                            alpha = if (hasNextChapter) 1f else disabledAlpha
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Chapter",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    )
    { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { isSystemBarVisible = !isSystemBarVisible }
                    )
                }
        ) {
            when {
                uiState.isChapterLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                uiState.chapterError != null -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${uiState.chapterError}")
                }

                uiState.chapterData != null -> {
                    val chapterData = uiState.chapterData
                    val baseUrl = chapterData.baseUrl
                    val hash = chapterData.chapter.hash
                    val pages = chapterData.chapter.dataSaver

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(pages.size) { idx ->
                            val pageUrl = "$baseUrl/data-saver/$hash/${pages[idx]}"
                            Log.d("ChapterReader", "Page URL: $pageUrl")
                            AsyncImage(
                                model = pageUrl,
                                contentDescription = "Page ${idx + 1}",
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentScale = ContentScale.FillWidth
                            )
                        }
                    }
                }
            }
        }
    }
}