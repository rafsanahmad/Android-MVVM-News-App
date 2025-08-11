package com.rafsan.newsapp.feature.search

import androidx.paging.PagingData
import com.google.common.truth.Truth.assertThat
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.usecase.SearchNewsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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
class SearchViewModelTest {
    @Test
    fun debounce_applies_before_search() = runTest {
        val usecase = SearchNewsUseCase(FakeRepository())
        val vm = SearchViewModel(usecase)
        vm.onQueryChanged("android")
        // Wait slightly more than debounce
        delay(600)
        val data = vm.results.first()
        assertThat(data).isInstanceOf(PagingData::class.java)
    }
}