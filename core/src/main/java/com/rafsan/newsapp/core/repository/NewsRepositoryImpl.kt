package com.rafsan.newsapp.core.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rafsan.newsapp.core.database.NewsDao
import com.rafsan.newsapp.core.database.NewsDatabase
import com.rafsan.newsapp.core.database.entity.NewsArticleEntity
import com.rafsan.newsapp.core.network.NewsApi
import com.rafsan.newsapp.core.network.NewsRemoteMediator
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private fun NewsArticleEntity.toDomain(): NewsArticle = NewsArticle(
    id = id,
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = com.rafsan.newsapp.domain.model.Source(sourceId, sourceName ?: ""),
    title = title,
    url = url,
    urlToImage = urlToImage
)

private fun NewsArticle.toEntity(): NewsArticleEntity = NewsArticleEntity(
    id = id,
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    sourceId = source?.id,
    sourceName = source?.name,
    title = title,
    url = url,
    urlToImage = urlToImage
)

class NewsRepositoryImpl(
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
            pagingSourceFactory = { dao.pagingSource() }
        ).flow
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

    override suspend fun saveNews(news: NewsArticle): Long = dao.upsert(news.toEntity())

    override fun getSavedNews(): Flow<List<NewsArticle>> = dao.getAllNews().map { list -> list.map { it.toDomain() } }

    override suspend fun deleteNews(news: NewsArticle) = dao.deleteNews(news.toEntity())

    override suspend fun deleteAllNews() = dao.deleteAllNews()
}

private class LocalPagingSource(
    private val dao: NewsDao
) : PagingSource<Int, NewsArticle>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsArticle> {
        return try {
            val items = dao.getAllNews()
            // This PagingSource is used only with RemoteMediator; data is loaded from DB via PagingSource query.
            LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NewsArticle>): Int? = null
}