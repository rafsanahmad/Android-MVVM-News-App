package com.rafsan.newsapp.feature.source

import app.cash.turbine.test
import com.rafsan.newsapp.domain.model.NewsSource
import com.rafsan.newsapp.domain.usecase.GetSourcesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SourceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getSourcesUseCase: GetSourcesUseCase = mockk()
    private lateinit var viewModel: SourceViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SourceViewModel(getSourcesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state is updated with sources on successful fetch`() = runBlocking {
        // Given
        val sources = listOf(
            NewsSource("id1", "name1", "desc1", "url1", "cat1", "lang1", "country1")
        )
        coEvery { getSourcesUseCase() } returns flowOf(sources)

        // When
        viewModel.getSources()

        // Then
        viewModel.state.test {
            assertEquals(SourceState.Success(sources), awaitItem())
        }
    }

    @Test
    fun `search query filters the sources`() = runBlocking {
        // Given
        val sources = listOf(
            NewsSource("id1", "Apple", "desc1", "url1", "cat1", "lang1", "country1"),
            NewsSource("id2", "Banana", "desc2", "url2", "cat2", "lang2", "country2")
        )
        coEvery { getSourcesUseCase() } returns flowOf(sources)
        viewModel.getSources()
        testDispatcher.scheduler.advanceUntilIdle()


        // When
        viewModel.onSearchQueryChanged("Apple")

        // Then
        viewModel.state.test {
            val successState = awaitItem() as SourceState.Success
            assertEquals(1, successState.sources.size)
            assertEquals("Apple", successState.sources.first().name)
        }
    }
}
