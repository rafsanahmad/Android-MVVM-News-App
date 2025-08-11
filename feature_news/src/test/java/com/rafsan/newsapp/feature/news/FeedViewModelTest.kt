package com.rafsan.newsapp.feature.news

import androidx.paging.PagingData
import com.google.common.truth.Truth.assertThat
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.usecase.GetTopHeadlinesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

private class FakeRepository : com.rafsan.newsapp.domain.repository.NewsRepository {
    override fun getTopHeadlines(countryCode: String) = MutableStateFlow(PagingData.from(listOf(NewsArticle(1, null, null, null, null, null, "T", "u", null))))
    override fun searchNews(query: String) = MutableStateFlow(PagingData.from(listOf(NewsArticle(1, null, null, null, null, null, "T", "u", null))))
    override suspend fun saveNews(news: NewsArticle): Long = 1
    override fun getSavedNews() = kotlinx.coroutines.flow.flowOf(emptyList<NewsArticle>())
    override suspend fun deleteNews(news: NewsArticle) {}
    override suspend fun deleteAllNews() {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {
    @Test
    fun headlines_emits_data() = runTest {
        val uc = GetTopHeadlinesUseCase(FakeRepository())
        val vm = FeedViewModel(uc)
        val first = vm.headlines.first()
        assertThat(first).isInstanceOf(PagingData::class.java)
    }
}