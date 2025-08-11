package com.rafsan.newsapp.feature.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.rafsan.newsapp.core.navigation.Screen
import com.rafsan.newsapp.domain.model.NewsArticle
import androidx.compose.ui.res.stringResource
import com.rafsan.newsapp.feature.news.R

@Composable
fun FeedRoute(navController: NavController, viewModel: FeedViewModel = hiltViewModel()) {
    val pagingItems = viewModel.headlines.collectAsLazyPagingItems()
    FeedScreen(
        state = pagingItems,
        onClick = { article ->
            navController.currentBackStackEntry?.savedStateHandle?.set("url", article.url)
            navController.currentBackStackEntry?.savedStateHandle?.set("title", article.title)
            navController.currentBackStackEntry?.savedStateHandle?.set("image", article.urlToImage)
            navController.navigate(Screen.Details.route)
        }
    )
}

@Composable
fun FeedScreen(
    state: androidx.paging.compose.LazyPagingItems<NewsArticle>,
    onClick: (NewsArticle) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            count = state.itemCount,
            key = state.itemKey { it.url ?: it.title ?: "" }
        ) { index ->
            val article = state[index]
            if (article == null) {
                Text(stringResource(R.string.loading), modifier = Modifier.padding(16.dp))
            } else {
                NewsRow(article, onClick)
            }
        }
        when (val refresh = state.loadState.refresh) {
            is androidx.paging.LoadState.Loading -> {
                item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
            }
            is androidx.paging.LoadState.Error -> {
                item { Text(stringResource(R.string.error_with_message, refresh.error.message ?: ""), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) }
            }
            else -> {}
        }
        when (val append = state.loadState.append) {
            is androidx.paging.LoadState.Loading -> {
                item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
            }
            is androidx.paging.LoadState.Error -> {
                item { Text(stringResource(R.string.error_with_message, append.error.message ?: ""), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) }
            }
            else -> {}
        }
    }
}

@Composable
private fun NewsRow(
    article: NewsArticle,
    onClick: (NewsArticle) -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.clickable { onClick(article) }.padding(16.dp)
    ) {
        AsyncImage(
            model = article.urlToImage,
            contentDescription = null,
        )
        androidx.compose.foundation.layout.Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(text = article.title ?: "", style = MaterialTheme.typography.titleMedium)
            Text(text = article.description ?: "", style = MaterialTheme.typography.bodyMedium)
        }
    }
}