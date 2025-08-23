package com.rafsan.newsapp.feature.source

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun SourceNewsScreen(
    navController: NavController,
    viewModel: SourceNewsViewModel = hiltViewModel()
) {
    val newsArticles = viewModel.newsState.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        if (newsArticles.loadState.refresh is LoadState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(newsArticles.itemCount) { index ->
                    val article = newsArticles[index]
                    if (article != null) {
                        NewsItem(
                            article = article,
                            onItemClick = {
                                val articleJson = Json.encodeToString(article)
                                val encodedArticleJson = Uri.encode(articleJson)
                                navController.navigate("details/$encodedArticleJson")
                            }
                        )
                    }
                }
                if (newsArticles.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
