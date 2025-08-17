package com.rafsan.newsapp.domain.usecase

import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageNewsFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    fun getFavorites(): Flow<List<NewsArticle>> = favoriteRepository.getFavorites()
    suspend fun addFavorite(article: NewsArticle): Long =
        favoriteRepository.addFavorite(article)

    suspend fun removeFavorite(article: NewsArticle) =
        favoriteRepository.removeFavorite(article)

    suspend fun isFavorite(articleUrl: String): Boolean = favoriteRepository.isFavorite(articleUrl)
}