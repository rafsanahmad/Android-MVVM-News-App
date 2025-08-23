package com.rafsan.newsapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.rafsan.newsapp.data.database.NewsDao
import com.rafsan.newsapp.data.database.NewsDatabase
import com.rafsan.newsapp.data.mapper.toDomain
import com.rafsan.newsapp.data.network.NewsApi
import com.rafsan.newsapp.data.repository.paging.FeedPagingSource
import com.rafsan.newsapp.data.repository.paging.SearchPagingSource
import com.rafsan.newsapp.data.repository.paging.SourceNewsPagingSource
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApi,
    private val db: NewsDatabase,
    private val dao: NewsDao,
    private val apiKey: String,
    private val pageSize: Int
) : NewsRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getTopHeadlines(countryCode: String): Flow<PagingData<NewsArticle>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = { FeedPagingSource(api, dao, countryCode, apiKey, pageSize) }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override fun searchNews(query: String, sources: String?): Flow<PagingData<NewsArticle>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = { SearchPagingSource(api, query, sources, apiKey, pageSize) }
        ).flow
    }

    override fun getNewsBySource(source: String): Flow<PagingData<NewsArticle>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = { SourceNewsPagingSource(api, source, apiKey) }
        ).flow
    }
}