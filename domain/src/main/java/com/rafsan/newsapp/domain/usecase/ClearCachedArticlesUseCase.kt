package com.rafsan.newsapp.domain.usecase

import com.rafsan.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class ClearCachedArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke() {
        newsRepository.clearCachedArticles()
    }
}
