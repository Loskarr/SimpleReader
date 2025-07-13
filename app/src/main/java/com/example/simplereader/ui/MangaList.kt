package com.example.simplereader.ui

import androidx.compose.foundation.layout.*
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.simplereader.data.model.MangaDexManga
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.ImeAction
import com.example.simplereader.presentation.MangaUiState
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow

@Composable
fun MangaList(
    mangaPagingData: Flow<PagingData<MangaDexManga>>?,
    modifier: Modifier = Modifier,
    uiState: MangaUiState,
    onSearchQueryChange: (String) -> Unit = {},
    onSortChange: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
    onRefresh: () -> Unit = {},
    navController: NavController? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val mangaItems = mangaPagingData?.collectAsLazyPagingItems()

    Column(modifier = modifier
            .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { onSearchQueryChange(it) },
                label = { Text("Search") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                trailingIcon = {
                    IconButton(onClick = { 
                        onSearch(uiState.searchQuery)
                        onRefresh()
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { 
                    onSearch(uiState.searchQuery)
                    onRefresh()
                })
            )

            Box {
                OutlinedTextField(
                    value = uiState.selectedSort,
                    onValueChange = {},
                    label = { Text("Sort by") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Sort options")
                        }
                    },
                    modifier = Modifier
                        .width(150.dp)
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    sortOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onSortChange(option)
                                expanded = false
                                onRefresh()
                            }
                        )
                    }
                }
            }
        }

        if (mangaItems != null) {
            val items = mangaItems
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(items.itemCount) { index ->
                    val manga = items[index]
                    if (manga != null) {
                        MangaItem(
                            manga = manga,
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                navController?.navigate("mangaDetail/${manga.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}
private val sortOptions = listOf(
    "Popular",
    "Relevance",
    "Created At",
    "Rating"
)



@Composable
fun MangaItem(
    manga: MangaDexManga,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    SetSystemBarsVisible(true)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Transparent),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(manga.getCoverArt()?.getCoverUrl(manga.id))
                    .crossfade(true)
                    .build(),
                contentDescription = "Cover for ${manga.getTitle()}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = manga.getTitle(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
