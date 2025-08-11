package com.rafsan.newsapp.core.repository

import androidx.paging.PagingData
import com.google.common.truth.Truth.assertThat
import com.rafsan.newsapp.core.database.NewsDao
import com.rafsan.newsapp.core.database.NewsDatabase
import com.rafsan.newsapp.core.database.entity.NewsArticleEntity
import com.rafsan.newsapp.core.network.NewsApi
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.model.NewsResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

private class FakeDao : NewsDao {
    private val items = MutableStateFlow(emptyList<NewsArticleEntity>())
    override suspend fun upsert(newsArticle: NewsArticleEntity): Long = 1
    override fun getAllNews() = items
    override fun pagingSource(): androidx.paging.PagingSource<Int, NewsArticleEntity> = 
        object : androidx.paging.PagingSource<Int, NewsArticleEntity>() {
            override suspend fun load(params: LoadParams<Int>) = LoadResult.Page(emptyList<NewsArticleEntity>(), null, null)
            override fun getRefreshKey(state: androidx.paging.PagingState<Int, NewsArticleEntity>) = null
        }
    override suspend fun deleteNews(newsArticle: NewsArticleEntity) {}
    override suspend fun deleteAllNews() {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class NewsRepositoryImplTest {
    @Test
    fun search_returns_paging_data() = runTest {
        val api = mock<NewsApi>()
        val db = mock<NewsDatabase>()
        val dao = FakeDao()
        
        whenever(api.searchNews("test", 1, 15, "key")).thenReturn(
            Response.success(NewsResponse("ok", 1, mutableListOf(
                NewsArticle(1, "A", "C", "D", "", null, "T", "u", "")
            )))
        )
        
        val repo = NewsRepositoryImpl(api, db, dao, "key", 15)
        val flow = repo.searchNews("test")
        val data = flow.first()
        assertThat(data).isInstanceOf(PagingData::class.java)
    }
}