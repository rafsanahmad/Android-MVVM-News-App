package com.rafsan.newsapp.feature.favorite

import androidx.lifecycle.ViewModel
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    repository: NewsRepository
) : ViewModel() {
    val favorites: Flow<List<NewsArticle>> = repository.getSavedNews()
}