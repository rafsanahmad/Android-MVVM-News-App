package com.rafsan.newsapp.domain.repository

import com.rafsan.newsapp.domain.model.NewsSource

interface SourceRepository {
    suspend fun getSources(): List<NewsSource>
}
