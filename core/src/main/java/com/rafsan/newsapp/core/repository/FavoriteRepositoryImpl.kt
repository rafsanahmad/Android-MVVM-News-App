package com.rafsan.newsapp.core.repository

import com.rafsan.newsapp.core.database.NewsDao
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.FavoriteRepository
import com.rafsan.newsapp.domain.mapper.toDomain
import com.rafsan.newsapp.domain.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao
) : FavoriteRepository {

    override fun getFavorites(): Flow<List<NewsArticle>> {
        return newsDao.getAllNews().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addFavorite(article: NewsArticle): Long {
        return newsDao.upsert(article.toEntity())
    }

    override suspend fun removeFavorite(article: NewsArticle) {
        newsDao.deleteNews(article.toEntity())
    }

    override suspend fun isFavorite(articleUrl: String): Boolean {
        return newsDao.getArticleByUrl(articleUrl) != null
    }

    override suspend fun removeAllFavorites() {
        newsDao.deleteAllNews()
    }
}