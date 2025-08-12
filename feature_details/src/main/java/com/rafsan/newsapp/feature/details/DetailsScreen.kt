package com.rafsan.newsapp.feature.details

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rafsan.newsapp.domain.model.NewsArticle
import timber.log.Timber
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp

@Composable
fun DetailsRoute(navController: NavController, viewModel: DetailsViewModel = hiltViewModel()) {
    val url = navController.previousBackStackEntry?.savedStateHandle?.get<String>("url")
    val title = navController.previousBackStackEntry?.savedStateHandle?.get<String>("title")
    val image = navController.previousBackStackEntry?.savedStateHandle?.get<String>("image")
    if (url.isNullOrBlank()) {
        Text(stringResource(R.string.invalid_article))
        return
    }
    DetailsScreen(url = url, onFavorite = {
        val article = NewsArticle(
            id = null, author = null, content = null, description = null, publishedAt = null,
            source = null, title = title, url = url, urlToImage = image
        )
        viewModel.onSaveFavorite(article)
    })
}

@Composable
private fun DetailsScreen(url: String, onFavorite: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val addedMessage = stringResource(R.string.added_to_favorites)
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(url)
            }
        }, modifier = Modifier.fillMaxSize())

        FloatingActionButton(
            onClick = {
                onFavorite()
                Timber.d("Favorited: %s", url)
                coroutineScope.launch { snackbarHostState.showSnackbar(message = addedMessage) }
            }, modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(R.string.favorite)
            )
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}