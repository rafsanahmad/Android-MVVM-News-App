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

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {
    @Test
    fun headlines_emits_data() = runTest {
        val uc = object : GetTopHeadlinesUseCase({ _: String -> MutableStateFlow(PagingData.from(listOf(NewsArticle(1, null, null, null, null, null, "T", "u", null)))) }) {}
        val vm = FeedViewModel(uc)
        val first = vm.headlines.first()
        assertThat(first).isInstanceOf(PagingData::class.java)
    }
}