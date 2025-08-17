package com.rafsan.newsapp.domain.repository

import com.rafsan.newsapp.domain.model.NewsArticle
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavorites(): Flow<List<NewsArticle>>
    suspend fun addFavorite(article: NewsArticle): Long
    suspend fun removeFavorite(article: NewsArticle)
    suspend fun isFavorite(articleUrl: String): Boolean
    suspend fun removeAllFavorites() // Corresponds to deleteAllNews
}