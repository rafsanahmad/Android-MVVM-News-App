/*
 * *
 *  * Created by Rafsan Ahmad on 8/23/25, 4:12PM
 *  * Copyright (c) 2025 . All rights reserved.
 *
 */

package com.rafsan.newsapp.feature.source.sourceNews

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.rafsan.newsapp.domain.model.NewsArticle
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceNewsScreen(
    navController: NavController,
    viewModel: SourceNewsViewModel = hiltViewModel()
) {
    val newsArticles = viewModel.newsState.collectAsLazyPagingItems()
    val sourceName = viewModel.sourceName

    SourceNewsContent(
        sourceName = sourceName,
        newsArticles = newsArticles,
        navController = navController,
        onSearchClick = {
            navController.navigate("search?sourceId=${viewModel.sourceId}")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceNewsContent(
    sourceName: String,
    newsArticles: LazyPagingItems<NewsArticle>,
    navController: NavController,
    onSearchClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = sourceName) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                    if (newsArticles.loadState.append.endOfPaginationReached) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No more item available")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SourceNewsScreenPreview() {
    val mockArticles = listOf(
        NewsArticle(
            author = "Author 1",
            title = "Title 1",
            description = "Description 1",
            url = "https://example.com/1",
            urlToImage = "https://example.com/image1.jpg",
            publishedAt = "2025-01-01T12:00:00Z",
            content = "Content 1",
            source = com.rafsan.newsapp.domain.model.Source(id = "id", name = "name")
        ),
        NewsArticle(
            author = "Author 2",
            title = "Title 2",
            description = "Description 2",
            url = "https://example.com/2",
            urlToImage = "https://example.com/image2.jpg",
            publishedAt = "2025-01-02T12:00:00Z",
            content = "Content 2",
            source = com.rafsan.newsapp.domain.model.Source(id = "id", name = "name")
        )
    )
    val pagingData = PagingData.from(mockArticles)
    val flow = flowOf(pagingData)
    val lazyPagingItems = flow.collectAsLazyPagingItems()

    SourceNewsContent(
        sourceName = "BBC News",
        newsArticles = lazyPagingItems,
        navController = rememberNavController(),
        onSearchClick = {}
    )
}
