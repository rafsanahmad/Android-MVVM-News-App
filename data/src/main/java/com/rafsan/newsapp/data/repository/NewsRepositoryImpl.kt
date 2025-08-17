package com.rafsan.newsapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.rafsan.newsapp.data.database.NewsDao
import com.rafsan.newsapp.data.database.NewsDatabase
import com.rafsan.newsapp.data.mapper.toDomain
import com.rafsan.newsapp.data.network.NewsApi
import com.rafsan.newsapp.data.repository.paging.NewsRemoteMediator
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
            remoteMediator = NewsRemoteMediator(countryCode, pageSize, apiKey, api, db),
            pagingSourceFactory = { dao.feedPagingSource() }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override fun searchNews(query: String): Flow<PagingData<NewsArticle>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = {
                object : PagingSource<Int, NewsArticle>() {
                    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsArticle> {
                        val page = params.key ?: 1
                        return try {
                            val response = api.searchNews(query, page, pageSize, apiKey)
                            val body = response.body()
                            val articles = body?.articles ?: emptyList()
                            val nextKey = if (articles.isEmpty()) null else page + 1
                            LoadResult.Page(
                                data = articles,
                                prevKey = if (page == 1) null else page - 1,
                                nextKey = nextKey
                            )
                        } catch (e: Exception) {
                            LoadResult.Error(e)
                        }
                    }

                    override fun getRefreshKey(state: PagingState<Int, NewsArticle>): Int? {
                        return state.anchorPosition
                    }
                }
            }
        ).flow
    }

    override suspend fun clearCachedArticles() {
        dao.deleteCachedArticles()
    }
}