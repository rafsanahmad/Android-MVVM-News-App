package com.rafsan.newsapp.feature.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.usecase.ManageNewsFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed interface for UI events
sealed interface FavoritesEvent {
    data class OnRemoveFavorite(val article: NewsArticle) : FavoritesEvent
    // Add other events here if needed, e.g., OnArticleClick, OnUndoRemove, etc.
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val manageFavoritesUseCase: ManageNewsFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesScreenState>(FavoritesScreenState.Loading)
    val uiState: StateFlow<FavoritesScreenState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    // Handle events from the UI
    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.OnRemoveFavorite -> {
                viewModelScope.launch {
                    manageFavoritesUseCase.removeFavorite(event.article)
                    // The Flow from getFavorites() should re-emit automatically if
                    // manageFavoritesUseCase.getFavorites() is a Flow that observes the underlying data source.
                    // If not, you might need to explicitly call loadFavorites() or update the state here.
                }
            }
            // Handle other events
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

    // The old onDeleteFavorite method is removed as its functionality
    // is now handled by onEvent(FavoritesEvent.OnRemoveFavorite(article))
}

// Ensure FavoritesScreenState is defined, matching what FavoritesScreen expects.
// If it's defined in FavoritesScreen.kt for previews, you might want to move it
// to a common location or ensure it's defined here as well.
// Example (make sure it matches your actual definition):
/*
sealed interface FavoritesScreenState {
    object Loading : FavoritesScreenState
    data class Success(val articles: List<NewsArticle>) : FavoritesScreenState
    object Empty : FavoritesScreenState
    // data class Error(val message: String) : FavoritesScreenState // Optional
}
*/
