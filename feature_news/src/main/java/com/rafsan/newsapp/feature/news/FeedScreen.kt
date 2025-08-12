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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
    val isInitialLoading = state.itemCount == 0 && state.loadState.refresh is androidx.paging.LoadState.Loading
    if (isInitialLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            count = state.itemCount,
            key = { index ->
                val a = state[index]
                a?.url ?: ((a?.title ?: "") + "#" + index)
            }
        ) { index ->
            val article = state[index]
            if (article != null) {
                NewsRow(article, onClick)
            }
        }
        when (val append = state.loadState.append) {
            is androidx.paging.LoadState.Loading -> {
                item { Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(article) }
            .padding(16.dp)
    ) {
        AsyncImage(
            model = article.urlToImage,
            contentDescription = null,
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}