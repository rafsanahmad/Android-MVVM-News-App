package com.rafsan.newsapp.data.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rafsan.newsapp.data.network.NewsApi
import com.rafsan.newsapp.domain.model.NewsArticle

class SearchPagingSource(
    private val api: NewsApi,
    private val query: String,
    private val sources: String?,
    private val apiKey: String,
    private val pageSize: Int
) : PagingSource<Int, NewsArticle>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsArticle> {
        val page = params.key ?: 1
        return try {
            val response = api.searchNews(query, page, pageSize, apiKey, sources)
            val articles = response.body()?.articles ?: emptyList()
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
