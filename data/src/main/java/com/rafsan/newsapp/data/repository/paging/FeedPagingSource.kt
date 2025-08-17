package com.rafsan.newsapp.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rafsan.newsapp.data.database.NewsDao
import com.rafsan.newsapp.data.mapper.toEntity
import com.rafsan.newsapp.data.database.entity.NewsArticleEntity
import com.rafsan.newsapp.data.network.NewsApi

class FeedPagingSource(
    private val api: NewsApi,
    private val newsDao: NewsDao,
    private val countryCode: String,
    private val apiKey: String,
    private val pageSize: Int
) : PagingSource<Int, NewsArticleEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsArticleEntity> {
        val page = params.key ?: 1
        return try {
            val response = api.getNews(countryCode, page, pageSize, apiKey)
            val body = response.body()
            val articles = body?.articles ?: emptyList()

            // De-duplicate by URL and filter out null URLs, then map to entity
            val entities = articles
                .filter { it.url != null }
                .distinctBy { it.url }
                .map { it.toEntity() }

            // Save to database for offline cache
            if (page == 1) {
                newsDao.deleteCachedArticles()
            }
            newsDao.upsert(entities)

            LoadResult.Page(
                data = entities,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NewsArticleEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
