package com.rafsan.newsapp.domain.usecase

import androidx.paging.PagingData
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class GetTopHeadlinesUseCase(private val repository: NewsRepository) {
    operator fun invoke(countryCode: String): Flow<PagingData<NewsArticle>> =
        repository.getTopHeadlines(countryCode)
}