package com.rafsan.newsapp.feature.details

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// Assuming R class is available here, if not, specific feature R class might be needed
// For example, com.rafsan.newsapp.feature.details.R
// For now, using a generic R, assuming it resolves to app.R or similar a R.string.something

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsRoute(
    navController: NavController,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = if (uiState is DetailScreenState.Success) {
                        (uiState as DetailScreenState.Success).article.title ?: stringResource(id = R.string.details_screen_title) // Example: R.string.details_screen_title
                    } else {
                        stringResource(id = R.string.details_screen_title) // Example: R.string.details_screen_title
                    }
                    Text(text = titleText, maxLines = 1)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back_button_desc)  // Example: R.string.back_button_desc
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState is DetailScreenState.Success) {
                val article = (uiState as DetailScreenState.Success).article
                if (article.url != null) { // Ensure article has a URL to be identifiable for favoriting
                    FloatingActionButton(onClick = {
                        viewModel.toggleFavorite()
                        coroutineScope.launch {
                            val message = if (viewModel.isFavorite.value) { // Check the latest state from ViewModel after toggle
                                context.getString(R.string.added_to_favorites) // Example string resource
                            } else {
                                context.getString(R.string.removed_from_favorites) // Example string resource
                            }
                            snackbarHostState.showSnackbar(message)
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(id = if (isFavorite) R.string.unfavorite_action_desc else R.string.favorite_action_desc) // Example
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is DetailScreenState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetailScreenState.Success -> {
                    if (state.article.url.isNullOrBlank()) {
                        Text(
                            text = stringResource(id = R.string.invalid_article_url_message), // Example
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        DetailWebView(url = state.article.url!!)
                    }
                }
                is DetailScreenState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
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
                        override fun onPageFinished(view: WebView,สวย: String) {
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