package com.rafsan.newsapp.domain.usecase

import androidx.paging.PagingData
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsBySourceUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    operator fun invoke(source: String): Flow<PagingData<NewsArticle>> {
        return newsRepository.getNewsBySource(source)
    }
}
