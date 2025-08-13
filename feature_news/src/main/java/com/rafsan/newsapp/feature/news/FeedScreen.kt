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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rafsan.newsapp.core.navigation.Screen
import com.rafsan.newsapp.domain.model.NewsArticle
import androidx.compose.ui.res.stringResource

@Composable
fun FeedRoute(navController: NavController, viewModel: FeedViewModel = hiltViewModel()) {
    val pagingItems = viewModel.headlines.collectAsLazyPagingItems()
    FeedScreen(
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
fun FeedScreen(
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
                    text = stringResource(com.rafsan.newsapp.feature.news.R.string.error_loading_feed, error.message ?: "Unknown error"),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            is LoadState.NotLoading -> {
                if (state.itemCount == 0) {
                    Text(
                        text = stringResource(com.rafsan.newsapp.feature.news.R.string.no_news_found),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            count = state.itemCount,
                            key = { index ->
                                val item = state.peek(index) // Use peek to avoid triggering load
                                item?.id ?: item?.url ?: "${item?.title ?: ""}_${index}"
                            }
                        ) { index ->
                            val article = state[index] // Access item, may trigger load
                            if (article != null) {
                                NewsRow(article, onClick)
                            }
                        }

                        when (val appendState = state.loadState.append) {
                            is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                            is LoadState.Error -> {
                                item {
                                    Text(
                                        stringResource(com.rafsan.newsapp.feature.news.R.string.error_loading_more_news, appendState.error.message ?: "Unknown error"),
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            else -> {} // NotLoading or EndOfPaginationReached
                        }
                        // Similarly handle prepend state if your PagingConfig supports it
                        // when (val prependState = state.loadState.prepend) { ... }
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
            contentDescription = article.title ?: stringResource(com.rafsan.newsapp.feature.news.R.string.news_article_image),
            modifier = Modifier.size(96.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier
            .padding(start = 12.dp)
            .weight(1f)) {
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
                maxLines = 3, // Slightly more description allowed
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}