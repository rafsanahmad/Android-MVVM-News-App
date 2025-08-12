package com.rafsan.newsapp.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.shape.RoundedCornerShape
import com.rafsan.newsapp.core.navigation.Screen
import com.rafsan.newsapp.domain.model.NewsArticle

@Composable
fun SearchRoute(navController: NavController, viewModel: SearchViewModel = hiltViewModel()) {
    val queryState = remember { mutableStateOf("") }
    val results = viewModel.results.collectAsLazyPagingItems()
    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = queryState.value,
            onValueChange = {
                queryState.value = it
                viewModel.onQueryChanged(it)
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.search)) },
            shape = RoundedCornerShape(50)
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(count = results.itemCount, key = { index ->
                val a = results[index]
                a?.url ?: ((a?.title ?: "") + "#" + index)
            }) { index ->
                val article = results[index]
                if (article != null) {
                    NewsRow(article) {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "url",
                            article.url
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "title",
                            article.title
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "image",
                            article.urlToImage
                        )
                        navController.navigate(Screen.Details.route)
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsRow(article: NewsArticle, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        AsyncImage(
            model = article.urlToImage,
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