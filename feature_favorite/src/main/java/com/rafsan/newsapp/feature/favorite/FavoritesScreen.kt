package com.rafsan.newsapp.feature.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun FavoritesRoute(navController: NavController, viewModel: FavoritesViewModel = hiltViewModel()) {
    val items by viewModel.favorites.collectAsState(initial = emptyList())
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items) { article ->
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.clickable { navController.navigate("details") }.padding(16.dp)
            ) {
                AsyncImage(model = article.urlToImage, contentDescription = null)
                androidx.compose.foundation.layout.Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(text = article.title ?: "", style = MaterialTheme.typography.titleMedium)
                    Text(text = article.description ?: "", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}