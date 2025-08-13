package com.rafsan.newsapp.feature.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.rafsan.newsapp.domain.model.NewsArticle
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val removeMessage = stringResource(R.string.snackbar_remove_favorite_message)
    val confirmAction = stringResource(R.string.snackbar_remove_confirm_action)

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is FavoritesScreenState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is FavoritesScreenState.Success -> {
                    val items = state.articles
                    // Note: Empty check within Success is redundant if Empty state is handled,
                    // but can be kept for robustness or specific UI for Success but empty.
                    if (items.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.no_favorite_articles_yet))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(items, key = { index, article ->
                                article.id ?: article.url ?: "${article.title ?: ""}_${index}"
                            }) { _, article ->
                                var dismissed by remember { mutableStateOf(false) }
                                if (!dismissed) {
                                    DismissibleItem(
                                        item = article,
                                        onDismiss = {
                                            dismissed = true
                                            coroutineScope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = removeMessage,
                                                    actionLabel = confirmAction,
                                                    withDismissAction = true
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    viewModel.onDeleteFavorite(article)
                                                } else {
                                                    dismissed = false
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                is FavoritesScreenState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_favorite_articles_found))
                    }
                }
                // Add is FavoritesScreenState.Error if defined in ViewModel
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissibleItem(item: NewsArticle, onDismiss: () -> Unit) {
    //val itemKey = item.id ?: item.url ?: item.title
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                onDismiss()
                true
            } else false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    stringResource(R.string.unfavorite),
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = item.urlToImage,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = item.title ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    val sample = listOf(
        NewsArticle(
            id = 1,
            author = "Author",
            content = "Content",
            description = "Description",
            publishedAt = "2024-01-01",
            source = null,
            title = "Sample Article",
            url = "https://example.com",
            urlToImage = null
        ),
        NewsArticle(
            id = 2,
            author = "Author 2",
            content = "Content 2",
            description = "Another description",
            publishedAt = "2024-01-02",
            source = null,
            title = "Another Article",
            url = "https://example.com/2",
            urlToImage = null
        )
    )

    // Stateless preview wrapper to minimize recomposition
    @Composable
    fun FavoritesList(articles: List<NewsArticle>, onDismiss: (NewsArticle) -> Unit) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(articles) { _, article ->
                DismissibleItem(item = article) { onDismiss(article) }
            }
        }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            FavoritesList(articles = sample, onDismiss = {})
        }
    }
}