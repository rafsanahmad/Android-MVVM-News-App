/*
 * *
 *  * Created by Rafsan Ahmad on 8/23/25, 4:11PM
 *  * Copyright (c) 2025 . All rights reserved.
 *
 */

package com.rafsan.newsapp.feature.source.sources

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rafsan.newsapp.R
import com.rafsan.newsapp.core.util.Constants
import com.rafsan.newsapp.core.util.getDomainName
import com.rafsan.newsapp.core.util.getFlagEmoji
import com.rafsan.newsapp.domain.model.NewsSource

@Composable
fun SourceScreen(
    navController: NavController,
    viewModel: SourceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    SourceScreenContent(
        sourceState = state,
        searchQuery = searchQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSourceClick = { source ->
            val encodedSourceName = java.net.URLEncoder.encode(source.name, "UTF-8")
            navController.navigate("source_news/${source.id}/${encodedSourceName}")
        }
    )
}

@Composable
fun SourceScreenContent(
    sourceState: SourceState,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSourceClick: (NewsSource) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChanged = onSearchQueryChanged
        )
        when (sourceState) {
            is SourceState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SourceState.Success -> {
                SourceList(
                    sources = sourceState.sources,
                    onSourceClick = onSourceClick
                )
            }

            is SourceState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = sourceState.message)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SourceScreenPreview(
    @PreviewParameter(SourcePreviewParameterProvider::class) sourceState: SourceState
) {
    SourceScreenContent(
        sourceState = sourceState,
        searchQuery = "",
        onSearchQueryChanged = {},
        onSourceClick = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = { Text("Search Sources") },
        singleLine = true
    )
}

@Composable
fun SourceList(
    sources: List<NewsSource>,
    onSourceClick: (NewsSource) -> Unit
) {
    LazyColumn {
        items(sources) { source ->
            SourceItem(source = source, onClick = { onSourceClick(source) })
        }
    }
}

@Composable
fun SourceItem(
    source: NewsSource,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val domain = getDomainName(source.url)
        if (domain != null) {
            AsyncImage(
                model = "${Constants.CLEARBIT_LOGO_API}$domain",
                contentDescription = source.name,
                modifier = Modifier.size(40.dp),
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                error = painterResource(id = R.drawable.ic_launcher_background)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Text(text = getFlagEmoji(source.country), modifier = Modifier.padding(end = 16.dp))
        Column {
            Text(text = source.name)
            Text(text = "Category: ${source.category}")
            Text(text = "Language: ${source.language}")
        }
    }
}
