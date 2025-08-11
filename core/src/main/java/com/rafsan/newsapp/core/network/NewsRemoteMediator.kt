package com.rafsan.newsapp.core.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rafsan.newsapp.core.database.NewsDatabase
import com.rafsan.newsapp.core.database.entity.NewsArticleEntity
import com.rafsan.newsapp.domain.model.NewsArticle
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
                LoadType.APPEND -> {
                    val loaded = state.pages.sumOf { it.data.size }
                    loaded / pageSize + 1
                }
            }

            val response = withContext(Dispatchers.IO) {
                api.getNews(countryCode, page, pageSize, apiKey)
            }
            val body = response.body()
            val articles = body?.articles ?: emptyList()

            val entities = articles.map {
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
                    urlToImage = it.urlToImage
                )
            }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.newsDao().deleteAllNews()
                }
                entities.forEach { db.newsDao().upsert(it) }
            }

            MediatorResult.Success(endOfPaginationReached = entities.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}