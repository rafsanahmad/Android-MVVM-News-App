package com.rafsan.newsapp.domain.usecase

import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.FavoriteRepository // New dependency
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageNewsFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository // Changed
) {
    fun getFavorites(): Flow<List<NewsArticle>> = favoriteRepository.getFavorites() // Changed
    suspend fun addFavorite(article: NewsArticle): Long =
        favoriteRepository.addFavorite(article) // Changed

    suspend fun removeFavorite(article: NewsArticle) =
        favoriteRepository.removeFavorite(article) // Changed

    suspend fun isFavorite(articleUrl: String): Boolean = favoriteRepository.isFavorite(articleUrl)
}