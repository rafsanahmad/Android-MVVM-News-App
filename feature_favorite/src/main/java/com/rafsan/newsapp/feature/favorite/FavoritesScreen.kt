package com.rafsan.newsapp.feature.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rafsan.newsapp.domain.model.NewsArticle
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material.DismissValue
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import androidx.compose.material.ExperimentalMaterialApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FavoritesRoute(navController: NavController, viewModel: FavoritesViewModel = hiltViewModel()) {
    val items by viewModel.favorites.collectAsState(initial = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val removeMessage = stringResource(R.string.snackbar_remove_favorite_message)
    val confirmAction = stringResource(R.string.snackbar_remove_confirm_action)

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            itemsIndexed(items, key = { index, article ->
                article.url ?: ((article.title ?: "") + "#" + index)
            }) { _, article ->
                var dismissed by remember { mutableStateOf(false) }
                if (!dismissed) {
                    DismissibleItem(
                        item = article,
                        onDismiss = {
                            dismissed = true
                            coroutineScope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = removeMessage,
                                    actionLabel = confirmAction,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DismissibleItem(item: NewsArticle, onDismiss: () -> Unit) {
    val dismissState = rememberDismissState(confirmStateChange = { value ->
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
            ) {
                Text(
                    stringResource(R.string.unfavorite),
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        dismissContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = item.urlToImage,
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
                        text = item.title ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    )
}