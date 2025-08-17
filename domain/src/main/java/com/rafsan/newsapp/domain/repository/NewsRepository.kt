package com.rafsan.newsapp.domain.repository

import androidx.paging.PagingData
import com.rafsan.newsapp.domain.model.NewsArticle
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getTopHeadlines(countryCode: String): Flow<PagingData<NewsArticle>>
    fun searchNews(query: String): Flow<PagingData<NewsArticle>>
}