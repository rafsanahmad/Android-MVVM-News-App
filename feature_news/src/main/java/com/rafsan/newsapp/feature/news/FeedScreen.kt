package com.rafsan.newsapp.feature.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.net.Uri
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rafsan.newsapp.core.navigation.Screen
import com.rafsan.newsapp.core.util.getErrorMessage
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.model.Source
import com.rafsan.newsapp.feature.news.model.supportedCountries
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun FeedScreen(navController: NavController, viewModel: FeedViewModel = hiltViewModel()) {
    val pagingItems = viewModel.headlines.collectAsLazyPagingItems()
    val isOnline by viewModel.isOnline.collectAsState()
    val selectedCountryCode by viewModel.selectedCountryCode.collectAsState()

    FeedScreenLayout(
        state = pagingItems,
        isOnline = isOnline,
        selectedCountryCode = selectedCountryCode,
        onCountrySelected = viewModel::selectCountry,
        onClick = { article ->
            val articleJson = Json.encodeToString(article)
            val encodedArticleJson = Uri.encode(articleJson)
            navController.navigate("details/$encodedArticleJson")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreenLayout(
    state: LazyPagingItems<NewsArticle>,
    isOnline: Boolean,
    selectedCountryCode: String,
    onCountrySelected: (String) -> Unit,
    onClick: (NewsArticle) -> Unit
) {
    val isRefreshing = state.loadState.refresh is LoadState.Loading
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                val selectedCountry = supportedCountries.find { it.code == selectedCountryCode }
                TextField(
                    value = selectedCountry?.let { "${it.flag} ${it.name}" } ?: "Select Country",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Country") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    supportedCountries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text("${country.flag} ${country.name}") },
                            onClick = {
                                onCountrySelected(country.code)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        if (!isOnline) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.network_unavailable),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { state.refresh() }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.loadState.refresh is LoadState.Error) {
                    val error = (state.loadState.refresh as LoadState.Error).error
                    val errorMessage = getErrorMessage(error = error, context = context)
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                if (state.itemCount == 0 && state.loadState.append.endOfPaginationReached && !isRefreshing) {
                    Text(
                        text = stringResource(R.string.no_news_found),
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            count = state.itemCount,
                            key = { index -> state.peek(index)?.url ?: index },
                            contentType = { "newsArticle" }
                        ) { index ->
                            val article = state[index]
                            if (article != null) {
                                NewsRow(article, onClick)
                            } else {
                                Spacer(
                                    modifier = Modifier
                                        .height(100.dp)
                                        .fillMaxWidth()
                                )
                            }
                        }

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
                                        val errorMessage = getErrorMessage(
                                            error = appendState.error,
                                            context = context
                                        )
                                        Text(
                                            text = errorMessage,
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
                text = article.source?.name ?: "",
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
private fun FeedScreenLayoutPreview() {
    val sample = listOf(
        NewsArticle(
            id = 1,
            author = "Author",
            content = "Content",
            description = "This is a sample news description to see how it looks in the preview. It might be a bit longer.",
            publishedAt = "2024-01-01",
            source = Source(id = "cnn", name = "CNN News"),
            title = "Sample Article Title - A Long Title to Test Ellipsis",
            url = "https://example.com",
            urlToImage = "https://via.placeholder.com/150"
        ),
        NewsArticle(
            id = 2,
            author = "Author 2",
            content = "Content 2",
            description = "Another short description for a news item.",
            publishedAt = "2024-01-02",
            source = Source(id = "bbc", name = "BBC World"),
            title = "Second Article",
            url = "https://example.com/2",
            urlToImage = "https://via.placeholder.com/150"
        )
    )
    val pagingItems = flowOf(PagingData.from(sample)).collectAsLazyPagingItems()
    MaterialTheme {
        FeedScreenLayout(
            state = pagingItems,
            isOnline = true,
            selectedCountryCode = "us",
            onCountrySelected = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Feed Screen Empty")
@Composable
private fun FeedScreenLayoutEmptyPreview() {
    val emptyPagingItems = flowOf(
        PagingData.empty<NewsArticle>(
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true)
            )
        )
    ).collectAsLazyPagingItems()
    MaterialTheme {
        FeedScreenLayout(
            state = emptyPagingItems,
            isOnline = true,
            selectedCountryCode = "us",
            onCountrySelected = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Feed Screen Loading")
@Composable
private fun FeedScreenLayoutLoadingPreview() {
    val loadingPagingItems = flowOf(
        PagingData.empty<NewsArticle>(
            sourceLoadStates = LoadStates(
                refresh = LoadState.Loading,
                append = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = false)
            )
        )
    ).collectAsLazyPagingItems()
    MaterialTheme {
        FeedScreenLayout(
            state = loadingPagingItems,
            isOnline = true,
            selectedCountryCode = "us",
            onCountrySelected = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Feed Screen Error")
@Composable
private fun FeedScreenLayoutErrorPreview() {
    val errorPagingItems = flowOf(
        PagingData.empty<NewsArticle>(
            sourceLoadStates = LoadStates(
                refresh = LoadState.Error(RuntimeException("Failed to load data!")),
                append = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = false)
            )
        )
    ).collectAsLazyPagingItems()
    MaterialTheme {
        FeedScreenLayout(
            state = errorPagingItems,
            isOnline = true,
            selectedCountryCode = "us",
            onCountrySelected = {},
            onClick = {}
        )
    }
}
