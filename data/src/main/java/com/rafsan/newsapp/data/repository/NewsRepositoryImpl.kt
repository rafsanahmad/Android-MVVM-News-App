package com.rafsan.newsapp.data.repository // Updated package

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.rafsan.newsapp.core.database.entity.NewsArticleEntity // From core.database.entity
import com.rafsan.newsapp.core.mapper.toDomain // From core.mapper
import com.rafsan.newsapp.data.database.NewsDao // From data.database
import com.rafsan.newsapp.data.database.NewsDatabase // From data.database
import com.rafsan.newsapp.data.network.NewsApi // From data.network
import com.rafsan.newsapp.data.repository.paging.ArticleRemoteMediator // From data.repository.paging
import com.rafsan.newsapp.domain.model.NewsArticle // From domain
import com.rafsan.newsapp.domain.repository.NewsRepository // From domain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApi,
    private val db: NewsDatabase,
    private val dao: NewsDao,
    private val apiKey: String, // This will be provided by RepositoryModule in data layer
    private val pageSize: Int // This will be provided by RepositoryModule in data layer
) : NewsRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getTopHeadlines(countryCode: String): Flow<PagingData<NewsArticle>> {
        val pagingSourceFactory = { dao.getNewsArticles() } // Assuming dao.getNewsArticles() returns PagingSource<Int, NewsArticleEntity>
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            remoteMediator = ArticleRemoteMediator(countryCode, pageSize, apiKey, api, db, dao),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override fun searchNews(query: String): Flow<PagingData<NewsArticle>> {
        // Assuming searchNews in NewsApi returns NewsResponse which contains ArticleDto
        // And ArticleDto has a toDomain() extension in core.mapper
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = {
                object : PagingSource<Int, NewsArticle>() {
                    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsArticle> {
                        val page = params.key ?: 1
                        return try {
                            val response = api.searchNews(query, page, pageSize, apiKey)
                            val articlesDto = response.articles ?: emptyList()
                            val articlesDomain = articlesDto.map { it.toDomain() } // Assuming ArticleDto has toDomain
                            val nextKey = if (articlesDomain.isEmpty()) null else page + 1
                            LoadResult.Page(
                                data = articlesDomain,
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
}