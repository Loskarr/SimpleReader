package com.example.simplereader.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.simplereader.presentation.MangaViewModel
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.max
import androidx.navigation.NavController
import com.example.simplereader.data.model.MangaDexManga
import com.example.simplereader.presentation.MangaUiState
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar

fun formatPublishAt(publishAt: String): String {
    return try {
        val odt = OffsetDateTime.parse(publishAt)
        odt.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
    } catch (e: Exception) {
        publishAt
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailScreen(
    mangaId: String,
    getMangaById: (String) -> MangaDexManga?,
    uiState: MangaUiState,
    navController: NavController,
) {
    val manga = remember(mangaId) { getMangaById(mangaId) }
    SetSystemBarsVisible(true)
    if (manga == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Manga not found", style = MaterialTheme.typography.titleLarge)
        }
        return
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(0),
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            Row(
                modifier = Modifier
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(manga.getCoverArt()?.getCoverUrl(manga.id))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Cover for ${manga.getTitle()}",
                    modifier = Modifier
                        .padding(bottom = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .fillMaxWidth(0.4f)
                )
                Column() {
                    Text(
                        text = manga.getTitle(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Card(modifier = Modifier.padding(top = 12.dp)) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text("Type:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text("Status:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text("Chapters:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    manga.type.replaceFirstChar {it.titlecase()},
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    manga.attributes.status.replaceFirstChar {it.titlecase()}
                                    , style = MaterialTheme.typography.bodyLarge)
                                Text(manga.attributes.lastChapter ?: "Unknown", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }

            var expanded by remember { mutableStateOf(false) }
            val maxLines = if (expanded) Int.MAX_VALUE else 4
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Text(
                    text = "Description:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                TextButton(onClick = { expanded = !expanded },
                ) {
                    Text(if (expanded) "Collapse" else "More")
                }
            }
            Text(
                text = manga.getDescription(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = maxLines,
                overflow = Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Chapters:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            val chapters = uiState.mangaFeed?.data
            if (chapters != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainer),

                ) {
                    items(chapters.size) { index ->
                        val chapter = chapters[index]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("chapterReader/${chapter.id}") },
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            )
                            {
                                Text(
                                    text = buildString {
                                        append("Chapter ${chapter.attributes.chapter ?: "?"}")
                                        val title = chapter.attributes.title
                                        if (!title.isNullOrBlank()) {
                                            append(" - $title")
                                        }
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = Ellipsis
                                )
                                Text(
                                    text = formatPublishAt(chapter.attributes.publishAt),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            } else {
                Text("No chapters found.", style = MaterialTheme.typography.bodySmall)
            }

        }
    }
}
