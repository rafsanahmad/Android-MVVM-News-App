package com.rafsan.newsapp.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
import com.rafsan.newsapp.domain.model.Source
import kotlinx.coroutines.flow.flowOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel = hiltViewModel()) {
    val query by viewModel.currentQuery.collectAsState()
    val pagingItems = viewModel.searchResults.collectAsLazyPagingItems()
    val focusManager = LocalFocusManager.current

    SearchScreenLayout(
        query = query,
        pagingItems = pagingItems,
        onQueryChanged = viewModel::onQueryChanged,
        navController = navController,
        focusManager = focusManager
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenLayout(
    query: String,
    pagingItems: LazyPagingItems<NewsArticle>,
    onQueryChanged: (String) -> Unit,
    navController: NavController,
    focusManager: FocusManager
) {
    Scaffold(
        topBar = {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(id = R.string.search_hint)) },
                shape = RoundedCornerShape(percent = 50),
                singleLine = true,
                leadingIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_navigate_back)
                        )
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChanged("") }) { // Clear query
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = stringResource(id = R.string.cd_clear_search)
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                }),
                colors = OutlinedTextFieldDefaults.colors()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HandlePagingContent(
                query = query,
                pagingItems = pagingItems,
                navController = navController
            )
        }
    }
}

@Composable
private fun HandlePagingContent(
    query: String,
    pagingItems: LazyPagingItems<NewsArticle>,
    navController: NavController
) {
    val context = LocalContext.current

    when (val refreshState = pagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            if (query.isNotBlank() && pagingItems.itemCount == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (query.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(id = R.string.start_typing_to_search),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        is LoadState.Error -> {
            if (query.isNotBlank()) {
                val errorMessage = getErrorMessage(error = refreshState.error, context = context)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        is LoadState.NotLoading -> {
            if (query.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(id = R.string.start_typing_to_search),
                        textAlign = TextAlign.Center
                    )
                }
            } else if (pagingItems.itemCount == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(id = R.string.no_results_found_for_query, query),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        count = pagingItems.itemCount,
                        key = { index ->
                            val item = pagingItems.peek(index)
                            item?.id ?: item?.url ?: "${item?.title ?: ""}_${index}"
                        }
                    ) { index ->
                        val article = pagingItems[index]
                        if (article != null) {
                            SearchNewsRow(article = article, onClick = {
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
                            })
                        }
                    }

                    when (val appendState = pagingItems.loadState.append) {
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
                                val errorMessage = getErrorMessage(error = appendState.error, context = context)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = errorMessage,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        is LoadState.NotLoading -> {
                            if (appendState.endOfPaginationReached) {
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

import android.content.Context
import retrofit2.HttpException
import java.io.IOException

private fun getErrorMessage(error: Throwable, context: Context): String {
    return when (error) {
        is IOException -> context.getString(R.string.error_network)
        is HttpException -> {
            val code = error.code()
            if (code in 500..599) {
                context.getString(R.string.error_server)
            } else {
                context.getString(R.string.error_http, code)
            }
        }
        else -> context.getString(R.string.error_unknown)
    }
}

@Composable
private fun SearchNewsRow(
    article: NewsArticle,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = article.urlToImage,
            contentDescription = article.title
                ?: stringResource(id = R.string.news_article_image_description),
            modifier = Modifier.size(100.dp),
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
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Previews
@Preview(showBackground = true, name = "Search News Row")
@Composable
fun SearchNewsRowPreview() {
    val sampleArticle = NewsArticle(
        id = 1,
        author = "Author Name",
        content = "Article content here...",
        description = "This is a sample news article description that might be a bit long to see how it wraps.",
        publishedAt = "2023-01-01T12:00:00Z",
        source = Source(id = "source-id", name = "Source Name"),
        title = "Sample News Article Title - Breaking News",
        url = "http://example.com/news/1",
        urlToImage = "https://via.placeholder.com/100"
    )
    MaterialTheme { // Replace with your app's actual theme if available
        SearchNewsRow(article = sampleArticle, onClick = {})
    }
}

@Preview(showBackground = true, name = "Search Screen - Empty")
@Composable
fun SearchScreenLayoutPreview_Empty() {
    val emptyPagingItems = flowOf(PagingData.empty<NewsArticle>())
        .collectAsLazyPagingItems()
    MaterialTheme {
        SearchScreenLayout(
            query = "",
            pagingItems = emptyPagingItems,
            onQueryChanged = {},
            navController = NavController(LocalContext.current),
            focusManager = LocalFocusManager.current
        )
    }
}

@Preview(showBackground = true, name = "Search Screen - With Results")
@Composable
fun SearchScreenLayoutPreview_WithResults() {
    val articles = List(5) { index ->
        NewsArticle(
            id = index,
            title = "Preview Article $index",
            description = "Description for article $index",
            url = "http://example.com/preview/$index",
            urlToImage = "https://via.placeholder.com/100",
            author = "Preview Author",
            content = "",
            publishedAt = "2023-01-01T12:00:00Z",
            source = Source("preview-src", "Preview Source")
        )
    }
    val pagingData = PagingData.from(articles)
    val pagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    MaterialTheme {
        SearchScreenLayout(
            query = "search query",
            pagingItems = pagingItems,
            onQueryChanged = {},
            navController = NavController(LocalContext.current),
            focusManager = LocalFocusManager.current
        )
    }
}
