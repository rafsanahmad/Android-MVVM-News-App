package com.rafsan.newsapp.feature.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rafsan.newsapp.domain.model.NewsArticle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesRoute(navController: NavController, viewModel: FavoritesViewModel = hiltViewModel()) {
    val items by viewModel.favorites.collectAsState(initial = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            items(items, key = { it.url ?: it.title ?: it.hashCode().toString() }) { article ->
                var dismissed by remember { mutableStateOf(false) }
                if (!dismissed) {
                    DismissibleItem(
                        item = article,
                        onDismiss = {
                            dismissed = true
                            LaunchedEffect(article) {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Remove from favorites?",
                                    actionLabel = "Confirm",
                                    withDismissAction = true
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.onDeleteFavorite(article)
                                } else {
                                    dismissed = false
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissibleItem(item: NewsArticle, onDismiss: () -> Unit) {
    val dismissState = rememberDismissState(confirmValueChange = { value ->
        if (value == DismissValue.DismissedToStart || value == DismissValue.DismissedToEnd) {
            onDismiss(); true
        } else false
    })
    SwipeToDismiss(
        state = dismissState,
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red),
                contentAlignment = Alignment.CenterEnd
            ) { Text("Unfavorite", color = Color.White, modifier = Modifier.padding(16.dp)) }
        },
        dismissContent = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                AsyncImage(model = item.urlToImage, contentDescription = null)
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(text = item.title ?: "", style = MaterialTheme.typography.titleMedium)
                    Text(text = item.description ?: "", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    )
}