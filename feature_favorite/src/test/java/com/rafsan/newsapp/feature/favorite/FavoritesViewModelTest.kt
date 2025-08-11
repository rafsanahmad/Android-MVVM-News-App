package com.rafsan.newsapp.feature.favorite

import androidx.paging.PagingData
import com.google.common.truth.Truth.assertThat
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import com.rafsan.newsapp.domain.usecase.DeleteFavoriteUseCase
import com.rafsan.newsapp.domain.usecase.GetFavoritesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

private class FakeRepository : NewsRepository {
    private val favorites = MutableStateFlow(listOf(
        NewsArticle(1, "A", "C", "D", "", null, "Title1", "url1", ""),
        NewsArticle(2, "B", "C2", "D2", "", null, "Title2", "url2", "")
    ))
    override fun getTopHeadlines(countryCode: String): Flow<PagingData<NewsArticle>> = MutableStateFlow(PagingData.empty())
    override fun searchNews(query: String): Flow<PagingData<NewsArticle>> = MutableStateFlow(PagingData.empty())
    override suspend fun saveNews(news: NewsArticle): Long = 1
    override fun getSavedNews(): Flow<List<NewsArticle>> = favorites
    override suspend fun deleteNews(news: NewsArticle) { 
        favorites.value = favorites.value.filter { it.url != news.url }
    }
    override suspend fun deleteAllNews() { favorites.value = emptyList() }
}

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
    @Test
    fun favorites_flow_emits_data() = runTest {
        val repo = FakeRepository()
        val vm = FavoritesViewModel(GetFavoritesUseCase(repo), DeleteFavoriteUseCase(repo))
        val items = vm.favorites.first()
        assertThat(items).hasSize(2)
        assertThat(items[0].title).isEqualTo("Title1")
    }

    @Test
    fun delete_favorite_removes_item() = runTest {
        val repo = FakeRepository()
        val vm = FavoritesViewModel(GetFavoritesUseCase(repo), DeleteFavoriteUseCase(repo))
        val initialItems = vm.favorites.first()
        assertThat(initialItems).hasSize(2)
        
        vm.onDeleteFavorite(initialItems[0])
        val updatedItems = vm.favorites.first()
        assertThat(updatedItems).hasSize(1)
        assertThat(updatedItems[0].title).isEqualTo("Title2")
    }
}