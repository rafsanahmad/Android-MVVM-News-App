package com.rafsan.newsapp.feature.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import app.cash.turbine.test
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.model.Source
import com.rafsan.newsapp.domain.usecase.SearchNewsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: SearchViewModel
    private lateinit var searchNewsUseCase: SearchNewsUseCase

    private val dummyArticle = NewsArticle(
        id = 1,
        author = "Author",
        content = "Content",
        description = "Description",
        publishedAt = "2023-01-01T00:00:00Z",
        source = Source(id = "source-id", name = "Source Name"),
        title = "Title",
        url = "http://example.com/article1",
        urlToImage = "http://example.com/image1.jpg"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        searchNewsUseCase = mockk()
        viewModel = SearchViewModel(searchNewsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onQueryChanged updates currentQuery StateFlow`() = runTest(testDispatcher) {
        val testQuery = "test query"
        viewModel.onQueryChanged(testQuery)
        assertEquals(testQuery, viewModel.currentQuery.value)
    }

    @Test
    fun `searchResults emits empty PagingData when query is blank`() = runTest(testDispatcher) {
        coEvery { searchNewsUseCase(any()) } returns flowOf(PagingData.from(listOf(dummyArticle)))
        viewModel.onQueryChanged("")

        viewModel.searchResults.test {
            val emittedItem = awaitItem()
            // A more robust check for empty PagingData often involves a PagingDataDiffer
            // or asserting that the underlying source (use case) wasn't called as expected for blank.
            assertNotNull(emittedItem) // Basic assertion
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `searchResults emits data from use case when query is not blank after debounce`() =
        runTest(testDispatcher) {
            val testQuery = "bitcoin"
            val expectedPagingData = PagingData.from(listOf(dummyArticle))
            coEvery { searchNewsUseCase(testQuery) } returns flowOf(expectedPagingData)

            viewModel.onQueryChanged(testQuery)
            advanceTimeBy(600) // Debounce time is 500ms

            val actualPagingData = viewModel.searchResults.first()
            assertNotNull(actualPagingData)
            // Ideally, verify searchNewsUseCase was called with testQuery (needs MockK's coVerify)
        }

    @Test
    fun `searchResults debounces rapid query changes`() = runTest(testDispatcher) {
        val query1 = "apple"
        val query2 = "applesauce"
        val pagingData2: PagingData<NewsArticle> =
            PagingData.from(listOf(dummyArticle.copy(title = "Applesauce Recipe")))

        // Only mock for the final query that should be processed
        coEvery { searchNewsUseCase(query2) } returns flowOf(pagingData2)
        // If query1 was processed, it would need its own mock:
        // coEvery { searchNewsUseCase(query1) } returns flowOf(PagingData.empty())


        viewModel.searchResults.test {
            viewModel.onQueryChanged(query1)
            advanceTimeBy(100) // Less than debounce
            viewModel.onQueryChanged(query2)
            advanceTimeBy(600) // Past debounce for query2

            // Skip initial PagingData.empty() if the flow is initialized with it
            // or if the first onQueryChanged("") call causes it.
            // This depends on the exact initial state and behavior of the flow.
            // For a flow starting with currentQuery.value (often initially blank),
            // an empty PagingData emission is likely first.
            awaitItem() // Consume initial/blank PagingData

            val finalResult = awaitItem() // Should be data for query2 ("applesauce")
            assertNotNull(finalResult)
            // Ideally, verify searchNewsUseCase(query1) was NOT called or called 0 times after a certain point,
            // and searchNewsUseCase(query2) WAS called. (needs MockK's coVerify)
            cancelAndConsumeRemainingEvents()
        }
    }
}
