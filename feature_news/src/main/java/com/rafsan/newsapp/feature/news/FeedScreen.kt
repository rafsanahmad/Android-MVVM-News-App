package com.rafsan.newsapp.feature.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rafsan.newsapp.core.navigation.Screen
import com.rafsan.newsapp.domain.model.NewsArticle
import kotlinx.coroutines.flow.flowOf

@Composable
fun FeedScreen(navController: NavController, viewModel: FeedViewModel = hiltViewModel()) {
    val pagingItems = viewModel.headlines.collectAsLazyPagingItems()
    FeedScreenLayout( // Renamed to avoid confusion with the NavController version
        state = pagingItems,
        onClick = { article ->
            // Consider passing only article ID or URL and fetching fresh data in details screen
            // to ensure data consistency if it can change.
            navController.currentBackStackEntry?.savedStateHandle?.set("url", article.url)
            navController.currentBackStackEntry?.savedStateHandle?.set("title", article.title)
            navController.currentBackStackEntry?.savedStateHandle?.set("image", article.urlToImage)
            // Potentially pass other necessary fields like content, publishedAt, source etc.
            navController.navigate(Screen.Details.route)
        }
    )
}

@Composable
fun FeedScreenLayout( // Renamed
    state: LazyPagingItems<NewsArticle>,
    onClick: (NewsArticle) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (val refreshState = state.loadState.refresh) {
            is LoadState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is LoadState.Error -> {
                val error = refreshState.error
                Text(
                    text = stringResource(
                        R.string.error_loading_feed,
                        error.localizedMessage ?: "Unknown error" // Use localizedMessage for better user-facing errors
                    ),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }

            is LoadState.NotLoading -> {
                if (state.itemCount == 0 && state.loadState.append.endOfPaginationReached) {
                    // This handles the case where the list is empty AND no more data will be loaded.
                    Text(
                        text = stringResource(R.string.no_news_found),
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            count = state.itemCount,
                            key = { index ->
                                val item = state.peek(index)
                                // Prioritize URL as it's more likely to be unique.
                                // If your NewsArticle.id is guaranteed unique and non-null, use item?.id
                                item?.url ?: item?.id ?: "article_${index}" // Fallback to index if both are null
                            },
                            contentType = { "newsArticle" } // Helps with recomposition performance
                        ) { index ->
                            val article = state[index] // Access item, may trigger load
                            if (article != null) {
                                NewsRow(article, onClick)
                            } else {
                                // Placeholder for items that are not yet loaded (null items)
                                // This can happen if placeholders are enabled in PagingConfig
                                // You can put a shimmer or a simple placeholder here if needed
                                Spacer(modifier = Modifier.height(100.dp).fillMaxWidth()) // Example placeholder height
                            }
                        }

                        // Append state handling (loading more, error, end of list)
                        state.loadState.append.let { appendState ->
                            when (appendState) {
                                is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }

                                is LoadState.Error -> {
                                    item {
                                        Text(
                                            stringResource(
                                                R.string.error_loading_more_news,
                                                appendState.error.localizedMessage ?: "Unknown error"
                                            ),
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                is LoadState.NotLoading -> {
                                    if (appendState.endOfPaginationReached && state.itemCount > 0) {
                                        item {
                                            Text(
                                                text = stringResource(R.string.no_more_news_available),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsRow(
    article: NewsArticle,
    onClick: (NewsArticle) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(article) }
            .padding(16.dp)
    ) {
        AsyncImage(
            model = article.urlToImage,
            contentDescription = article.title
                ?: stringResource(R.string.news_article_image),
            modifier = Modifier.size(96.dp),
            contentScale = ContentScale.Crop,
            // Add placeholder and error drawables for better UX
            // placeholder = painterResource(id = R.drawable.ic_placeholder_image),
            // error = painterResource(id = R.drawable.ic_error_image)
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f) // Ensure Column takes available width
        ) {
            Text(
                text = article.title ?: "",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = article.source?.name ?: "", // Display source name if available
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = article.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, name = "Feed Screen with Data")
@Composable
private fun FeedScreenLayoutPreview() { // Renamed to match composable
    val sample = listOf(
        NewsArticle(
            id = 1,
            author = "Author",
            content = "Content",
            description = "This is a sample news description to see how it looks in the preview. It might be a bit longer.",
            publishedAt = "2024-01-01",
            source = com.rafsan.newsapp.domain.model.Source(id= "cnn", name="CNN News"),
            title = "Sample Article Title - A Long Title to Test Ellipsis",
            url = "https://example.com",
            urlToImage = "https://via.placeholder.com/150" // Placeholder image URL
        ),
        NewsArticle(
            id = 2,
            author = "Author 2",
            content = "Content 2",
            description = "Another short description for a news item.",
            publishedAt = "2024-01-02",
            source = com.rafsan.newsapp.domain.model.Source(id="bbc", name="BBC World"),
            title = "Second Article",
            url = "https://example.com/2",
            urlToImage = "https://via.placeholder.com/150"
        )
    )
    val pagingItems = flowOf(PagingData.from(sample)).collectAsLazyPagingItems()
    // Simulate end of pagination for preview if needed by adjusting PagingData
    // val pagingItemsEndOfList = flowOf(PagingData.from(sample, LoadStates(refresh = LoadState.NotLoading(false), prepend = LoadState.NotLoading(false), append = LoadState.NotLoading(true)))).collectAsLazyPagingItems()

    MaterialTheme { // Wrap with your app's theme
        FeedScreenLayout(state = pagingItems, onClick = {})
    }
}

@Preview(showBackground = true, name = "Feed Screen Empty")
@Composable
private fun FeedScreenLayoutEmptyPreview() {
    val emptyPagingItems = flowOf(PagingData.empty<NewsArticle>(
        sourceLoadStates = androidx.paging.LoadStates(
            refresh = LoadState.NotLoading(endOfPaginationReached = true), // Indicate that loading is done and list is empty
            append = LoadState.NotLoading(endOfPaginationReached = true),
            prepend = LoadState.NotLoading(endOfPaginationReached = true)
        )
    )).collectAsLazyPagingItems()
    MaterialTheme {
         FeedScreenLayout(state = emptyPagingItems, onClick = {})
    }
}

@Preview(showBackground = true, name = "Feed Screen Loading")
@Composable
private fun FeedScreenLayoutLoadingPreview() {
    val loadingPagingItems = flowOf(PagingData.empty<NewsArticle>(
         sourceLoadStates = androidx.paging.LoadStates(
            refresh = LoadState.Loading,
            append = LoadState.NotLoading(endOfPaginationReached = false),
            prepend = LoadState.NotLoading(endOfPaginationReached = false)
        )
    )).collectAsLazyPagingItems()
     MaterialTheme {
        FeedScreenLayout(state = loadingPagingItems, onClick = {})
    }
}

@Preview(showBackground = true, name = "Feed Screen Error")
@Composable
private fun FeedScreenLayoutErrorPreview() {
    val errorPagingItems = flowOf(PagingData.empty<NewsArticle>(
        sourceLoadStates = androidx.paging.LoadStates(
            refresh = LoadState.Error(RuntimeException("Failed to load data!")),
            append = LoadState.NotLoading(endOfPaginationReached = false),
            prepend = LoadState.NotLoading(endOfPaginationReached = false)
        )
    )).collectAsLazyPagingItems()
    MaterialTheme {
        FeedScreenLayout(state = errorPagingItems, onClick = {})
    }
}
