package com.rafsan.newsapp.domain.usecase

import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository

class SaveFavoriteUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(article: NewsArticle): Long = repository.saveNews(article)
}