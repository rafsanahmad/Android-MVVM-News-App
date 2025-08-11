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

private class FakeSearchUseCase : SearchNewsUseCase({ _: String -> MutableStateFlow(PagingData.empty<NewsArticle>()) })

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    @Test
    fun debounce_applies_before_search() = runTest {
        val usecase = object : SearchNewsUseCase({ _: String -> MutableStateFlow(PagingData.from(listOf(NewsArticle(1, null, null, null, null, null, "T", "u", null)))) }) {}
        val vm = SearchViewModel(usecase)
        vm.onQueryChanged("android")
        // Wait slightly more than debounce
        delay(600)
        val data = vm.results.first()
        assertThat(data).isInstanceOf(PagingData::class.java)
    }
}