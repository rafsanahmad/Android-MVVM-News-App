package com.rafsan.newsapp.feature.favorite

import com.rafsan.newsapp.domain.model.NewsArticle

sealed interface FavoritesEvent {
    data class OnRemoveFavorite(val article: NewsArticle) : FavoritesEvent
    data class OnUndoRemoveFavorite(val article: NewsArticle) : FavoritesEvent
}

sealed class FavoritesScreenState {
    object Loading : FavoritesScreenState()
    data class Success(val articles: List<NewsArticle>) : FavoritesScreenState()
    object Empty : FavoritesScreenState()
    data class Error(val message: String) : FavoritesScreenState()
}