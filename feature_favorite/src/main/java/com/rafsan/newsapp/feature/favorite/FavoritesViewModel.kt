package com.rafsan.newsapp.feature.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.usecase.ManageNewsFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val manageFavoritesUseCase: ManageNewsFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesScreenState>(FavoritesScreenState.Loading)
    val uiState: StateFlow<FavoritesScreenState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.OnRemoveFavorite -> {
                viewModelScope.launch {
                    manageFavoritesUseCase.removeFavorite(event.article)
                }
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            manageFavoritesUseCase.getFavorites()
                .catch { e ->
                    _uiState.value = FavoritesScreenState.Error(e.message ?: "Unknown error loading favorites")
                }
                .map { articles ->
                    if (articles.isEmpty()) {
                        FavoritesScreenState.Empty
                    } else {
                        FavoritesScreenState.Success(articles)
                    }
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }
}
