package com.rafsan.newsapp.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rafsan.newsapp.data.network.NewsApi
import com.rafsan.newsapp.domain.model.NewsArticle
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SourceNewsPagingSource @Inject constructor(
    private val newsApi: NewsApi,
    private val source: String,
    private val apiKey: String
) : PagingSource<Int, NewsArticle>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsArticle> {
        val page = params.key ?: 1
        return try {
            val response = newsApi.searchNews(
                searchQuery = "",
                pageNumber = page,
                pageSize = params.loadSize,
                apiKey = apiKey,
                sources = source
            )
            val articles = response.body()?.articles ?: emptyList()
            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NewsArticle>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
