package com.rafsan.newsapp.feature.details

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.model.Source

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailsViewEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(context.getString(effect.message))
                }
            }
        }
    }

    DetailsScreenContent(
        uiState = uiState,
        isFavorite = isFavorite,
        snackbarHostState = snackbarHostState,
        onFavoriteClicked = viewModel::onFavoriteClicked,
        onBack = { navController.navigateUp() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreenContent(
    uiState: DetailScreenState,
    isFavorite: Boolean,
    snackbarHostState: SnackbarHostState,
    onFavoriteClicked: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = if (uiState is DetailScreenState.Success) {
                        uiState.article.title ?: stringResource(id = R.string.details_screen_title)
                    } else {
                        stringResource(id = R.string.details_screen_title)
                    }
                    Text(text = titleText, maxLines = 1)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_desc)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState is DetailScreenState.Success) {
                val article = uiState.article
                if (article.url != null) {
                    FloatingActionButton(onClick = onFavoriteClicked) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(id = if (isFavorite) R.string.unfavorite_action_desc else R.string.favorite_action_desc)
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is DetailScreenState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is DetailScreenState.Success -> {
                    if (state.article.url.isNullOrBlank()) {
                        Text(
                            text = stringResource(id = R.string.invalid_article_url_message),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        DetailWebView(url = state.article.url!!)
                    }
                }

                is DetailScreenState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Details Screen Success")
@Composable
private fun DetailsScreenSuccessPreview() {
    val article = NewsArticle(
        author = "Author",
        content = "Content",
        description = "Description",
        publishedAt = "2024-01-01T12:00:00Z",
        source = Source(id = "cnn", name = "CNN"),
        title = "A long title to test how it looks in the app bar",
        url = "https://www.google.com",
        urlToImage = ""
    )
    MaterialTheme {
        DetailsScreenContent(
            uiState = DetailScreenState.Success(article),
            isFavorite = false,
            snackbarHostState = remember { SnackbarHostState() },
            onFavoriteClicked = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Details Screen Loading")
@Composable
private fun DetailsScreenLoadingPreview() {
    MaterialTheme {
        DetailsScreenContent(
            uiState = DetailScreenState.Loading,
            isFavorite = false,
            snackbarHostState = remember { SnackbarHostState() },
            onFavoriteClicked = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Details Screen Error")
@Composable
private fun DetailsScreenErrorPreview() {
    MaterialTheme {
        DetailsScreenContent(
            uiState = DetailScreenState.Error("Something went wrong"),
            isFavorite = false,
            snackbarHostState = remember { SnackbarHostState() },
            onFavoriteClicked = {},
            onBack = {}
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun DetailWebView(url: String) {
    var isLoading by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView, arg: String) {
                            isLoading = false
                        }
                    }
                    settings.javaScriptEnabled = true
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}