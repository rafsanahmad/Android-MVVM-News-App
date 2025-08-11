package com.rafsan.newsapp.domain.usecase

import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class GetFavoritesUseCase(private val repository: NewsRepository) {
    operator fun invoke(): Flow<List<NewsArticle>> = repository.getSavedNews()
}