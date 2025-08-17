package com.rafsan.newsapp.data.repository.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rafsan.newsapp.data.database.NewsDatabase
import com.rafsan.newsapp.data.database.entity.NewsArticleEntity
import com.rafsan.newsapp.data.network.NewsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val countryCode: String,
    private val pageSize: Int,
    private val apiKey: String,
    private val api: NewsApi,
    private val db: NewsDatabase
) : RemoteMediator<Int, NewsArticleEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsArticleEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.pages.size + 1
            }

            val response = withContext(Dispatchers.IO) {
                api.getNews(countryCode, page, pageSize, apiKey)
            }
            val body = response.body()
            val articles = body?.articles ?: emptyList()

            val entities = articles
                .filter { it.url != null } // Ensure URL is not null
                .distinctBy { it.url } // De-duplicate by URL
                .map {
                    NewsArticleEntity(
                        id = null,
                    author = it.author,
                    content = it.content,
                    description = it.description,
                    publishedAt = it.publishedAt,
                    sourceId = it.source?.id,
                    sourceName = it.source?.name,
                    title = it.title,
                    url = it.url,
                    urlToImage = it.urlToImage,
                    isFavorite = false // Explicitly set as not favorite
                )
                }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // Delete only cached articles, not favorites
                    db.newsDao().deleteCachedArticles()
                }
                entities.forEach { db.newsDao().upsert(it) }
            }

            MediatorResult.Success(endOfPaginationReached = entities.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}


