package com.rafsan.newsapp.feature.favorite

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.model.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
                    if (items.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.no_favorite_articles_yet))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            items(
                                items = items,
                                key = { article: NewsArticle ->
                                    article.url ?: article.id ?: article.title
                                    ?: "favorite_article_${article.hashCode()}"
                                },
                                contentType = { "favoriteArticle" }
                            ) { article: NewsArticle ->
                                DismissibleFavoriteItem(
                                    article = article,
                                    viewModel = viewModel, // Pass the actual viewModel instance
                                    snackbarHostState = snackbarHostState,
                                    coroutineScope = coroutineScope
                                )
                                if (items.indexOf(article) < items.lastIndex) {
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }

                is FavoritesScreenState.Empty -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.no_favorite_articles_found))
                    }
                }
                is FavoritesScreenState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class) // Changed from ExperimentalMaterial3Api
@Composable
private fun DismissibleFavoriteItem(
    article: NewsArticle,
    viewModel: FavoritesViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val currentArticle by rememberUpdatedState(article)
    val removeMessage = stringResource(R.string.snackbar_remove_favorite_message)
    val undoAction = stringResource(R.string.undo)

    // Using rememberDismissState from M2
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                coroutineScope.launch {
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = removeMessage,
                        actionLabel = undoAction,
                        duration = SnackbarDuration.Short
                    )
                    if (snackbarResult != SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(FavoritesEvent.OnRemoveFavorite(currentArticle))
                    }
                }
                return@rememberDismissState true
            }
            false
        }
    )

    // Using SwipeToDismiss from M2
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
        background = {
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    DismissValue.Default -> Color.Transparent
                    else -> MaterialTheme.colorScheme.errorContainer
                },
                label = "background color"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    stringResource(R.string.unfavorite),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    ) {
        FavoriteItemRow(article = currentArticle)
    }
}

@Composable
private fun FavoriteItemRow(article: NewsArticle) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = article.urlToImage,
            contentDescription = stringResource(R.string.article_image_description),
            modifier = Modifier.size(96.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = article.title ?: "",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = article.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            article.source?.name?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// --- Preview Section ---

@Preview(showBackground = true, name = "Favorite Item Row Preview")
@Composable
private fun FavoriteItemRowPreview() {
    val sampleArticle = NewsArticle(
        id = 1,
        author = "Author X",
        content = "Some interesting content here.",
        description = "This is a sample description of a news article that might be a bit long and could overflow.",
        publishedAt = "2024-07-30T10:00:00Z",
        source = Source(id = "src-id", name = "Sample News Source"),
        title = "Sample Favorite Article Title - Lorem Ipsum Dolor Sit Amet",
        url = "https://example.com/sample-article",
        urlToImage = "https://example.com/image.jpg"
    )
    MaterialTheme {
        FavoriteItemRow(article = sampleArticle)
    }
}
