package com.rafsan.newsapp.feature.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.usecase.ManageNewsFavoriteUseCase // Changed import
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed class for UI State
sealed class FavoritesScreenState {
    object Loading : FavoritesScreenState()
    data class Success(val articles: List<NewsArticle>) : FavoritesScreenState()
    object Empty : FavoritesScreenState()
    // data class Error(val message: String) : FavoritesScreenState() 
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val manageFavoritesUseCase: ManageNewsFavoriteUseCase // Changed to new use case
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesScreenState>(FavoritesScreenState.Loading)
    val uiState: StateFlow<FavoritesScreenState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            manageFavoritesUseCase.getFavorites() // Use new use case method
                .catch { e -> 
                    // _uiState.value = FavoritesScreenState.Error(e.message ?: "Unknown error loading favorites")
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

    fun onDeleteFavorite(article: NewsArticle) {
        viewModelScope.launch {
            manageFavoritesUseCase.removeFavorite(article) // Use new use case method
            // The Flow from getFavorites() should re-emit, updating the UI state.
        }
    }
}