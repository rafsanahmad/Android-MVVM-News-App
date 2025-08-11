package com.rafsan.newsapp.feature

import MainCoroutineRule
import androidx.paging.PagingData
import com.google.common.truth.Truth.assertThat
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import com.rafsan.newsapp.feature.news.FeedViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

class FakeNewsRepository : NewsRepository {
    private val fakeItems = MutableStateFlow(PagingData.from(listOf(
        NewsArticle(1, "A", "C", "D", "", null, "T1", "u1", ""),
        NewsArticle(2, "B", "C2", "D2", "", null, "T2", "u2", "")
    )))
    override fun getTopHeadlines(countryCode: String): Flow<PagingData<NewsArticle>> = fakeItems
    override fun searchNews(query: String): Flow<PagingData<NewsArticle>> = fakeItems
    override suspend fun saveNews(news: NewsArticle): Long = 1
    override fun getSavedNews() = kotlinx.coroutines.flow.flowOf(emptyList<NewsArticle>())
    override suspend fun deleteNews(news: NewsArticle) {}
    override suspend fun deleteAllNews() {}
}

@ExperimentalCoroutinesApi
class FeedViewModelTest {
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Test
    fun headlines_emits_data() = coroutineRule.runBlockingTest {
        val vm = FeedViewModel(FakeNewsRepository())
        val first = vm.headlines.first()
        assertThat(first).isInstanceOf(PagingData::class.java)
    }
}