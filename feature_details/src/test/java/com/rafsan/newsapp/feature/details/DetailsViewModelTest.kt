package com.rafsan.newsapp.feature.details

import androidx.paging.PagingData
import com.google.common.truth.Truth.assertThat
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import com.rafsan.newsapp.domain.usecase.SaveFavoriteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

private class FakeRepository : NewsRepository {
    var savedCount = 0
    override fun getTopHeadlines(countryCode: String): Flow<PagingData<NewsArticle>> = MutableStateFlow(PagingData.empty())
    override fun searchNews(query: String): Flow<PagingData<NewsArticle>> = MutableStateFlow(PagingData.empty())
    override suspend fun saveNews(news: NewsArticle): Long { savedCount++; return savedCount.toLong() }
    override fun getSavedNews(): Flow<List<NewsArticle>> = MutableStateFlow(emptyList())
    override suspend fun deleteNews(news: NewsArticle) {}
    override suspend fun deleteAllNews() {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {
    @Test
    fun save_favorite_calls_usecase() = runTest {
        val repo = FakeRepository()
        val vm = DetailsViewModel(SaveFavoriteUseCase(repo))
        val article = NewsArticle(1, "A", "C", "D", "", null, "Title", "url", "")
        
        vm.onSaveFavorite(article)
        assertThat(repo.savedCount).isEqualTo(1)
    }
}