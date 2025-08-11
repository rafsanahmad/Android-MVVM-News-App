package com.rafsan.newsapp.domain.usecase

import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository

class DeleteFavoriteUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(article: NewsArticle) = repository.deleteNews(article)
}