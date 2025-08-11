package com.rafsan.newsapp.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage

@Composable
fun SearchRoute(navController: NavController, viewModel: SearchViewModel = hiltViewModel()) {
    val queryState = remember { mutableStateOf("") }
    val results = viewModel.results.collectAsLazyPagingItems()
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = queryState.value,
            onValueChange = {
                queryState.value = it
                viewModel.onQueryChanged(it)
            },
            modifier = Modifier.padding(16.dp),
            label = { Text("Search") }
        )
        LazyColumn {
            items(count = results.itemCount, key = results.itemKey { it.url ?: it.title ?: "" }) { index ->
                val article = results[index]
                if (article != null) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.clickable { navController.navigate("details") }.padding(16.dp)
                    ) {
                        AsyncImage(model = article.urlToImage, contentDescription = null)
                        androidx.compose.foundation.layout.Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(text = article.title ?: "")
                            Text(text = article.description ?: "")
                        }
                    }
                }
            }
        }
    }
}