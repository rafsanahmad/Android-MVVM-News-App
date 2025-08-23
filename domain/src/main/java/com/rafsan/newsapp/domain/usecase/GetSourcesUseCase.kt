package com.rafsan.newsapp.domain.usecase

import com.rafsan.newsapp.domain.model.NewsSource
import com.rafsan.newsapp.domain.repository.SourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSourcesUseCase @Inject constructor(
    private val sourceRepository: SourceRepository
) {
    operator fun invoke(): Flow<List<NewsSource>> = flow {
        emit(sourceRepository.getSources())
    }
}
