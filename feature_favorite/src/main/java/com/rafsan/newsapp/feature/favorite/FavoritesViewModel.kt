package com.rafsan.newsapp.feature.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.newsapp.domain.usecase.ManageNewsFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
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

            is FavoritesEvent.OnUndoRemoveFavorite -> {
                viewModelScope.launch {
                    manageFavoritesUseCase.addFavorite(event.article)
                }
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            manageFavoritesUseCase.getFavorites()
                .catch { e ->
                    _uiState.value =
                        FavoritesScreenState.Error(e.message ?: "Unknown error loading favorites")
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
