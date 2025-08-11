package com.rafsan.newsapp.feature.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.usecase.DeleteFavoriteUseCase
import com.rafsan.newsapp.domain.usecase.GetFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavorites: GetFavoritesUseCase,
    private val deleteFavorite: DeleteFavoriteUseCase
) : ViewModel() {
    val favorites: Flow<List<NewsArticle>> = getFavorites()

    fun onDeleteFavorite(article: NewsArticle) {
        viewModelScope.launch { deleteFavorite(article) }
    }
}