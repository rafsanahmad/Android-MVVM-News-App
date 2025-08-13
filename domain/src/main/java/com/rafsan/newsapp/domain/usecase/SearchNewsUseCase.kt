package com.rafsan.newsapp.domain.usecase

import androidx.paging.PagingData
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchNewsUseCase @Inject constructor(private val repository: NewsRepository) {
    operator fun invoke(query: String): Flow<PagingData<NewsArticle>> =
        repository.searchNews(query)
}