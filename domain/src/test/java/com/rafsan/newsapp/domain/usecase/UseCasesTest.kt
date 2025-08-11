package com.rafsan.newsapp.domain.usecase

import androidx.paging.PagingData
import com.google.common.truth.Truth.assertThat
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

private class FakeRepo : NewsRepository {
    private val paging = MutableStateFlow(PagingData.from(listOf(NewsArticle(1, null, null, null, null, null, "T", "u", null))))
    private val favorites = MutableStateFlow(emptyList<NewsArticle>())
    override fun getTopHeadlines(countryCode: String): Flow<PagingData<NewsArticle>> = paging
    override fun searchNews(query: String): Flow<PagingData<NewsArticle>> = paging
    override suspend fun saveNews(news: NewsArticle): Long { favorites.value = favorites.value + news; return 1 }
    override fun getSavedNews(): Flow<List<NewsArticle>> = favorites
    override suspend fun deleteNews(news: NewsArticle) { favorites.value = favorites.value - news }
    override suspend fun deleteAllNews() { favorites.value = emptyList() }
}

@OptIn(ExperimentalCoroutinesApi::class)
class UseCasesTest {
    @Test
    fun getTopHeadlines_returnsPagingData() = runTest {
        val uc = GetTopHeadlinesUseCase(FakeRepo())
        val data = uc("us").first()
        assertThat(data).isInstanceOf(PagingData::class.java)
    }

    @Test
    fun favorites_add_and_remove() = runTest {
        val repo = FakeRepo()
        val save = SaveFavoriteUseCase(repo)
        val get = GetFavoritesUseCase(repo)
        val del = DeleteFavoriteUseCase(repo)
        val news = NewsArticle(2, null, null, null, null, null, "Title2", "u2", null)
        save(news)
        assertThat(get().first()).contains(news)
        del(news)
        assertThat(get().first()).doesNotContain(news)
    }
}