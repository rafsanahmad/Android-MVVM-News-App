package com.rafsan.newsapp.domain.usecase

import com.rafsan.newsapp.domain.model.NewsSource
import com.rafsan.newsapp.domain.repository.SourceRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSourcesUseCaseTest {

    private val sourceRepository: SourceRepository = mockk()
    private val getSourcesUseCase = GetSourcesUseCase(sourceRepository)

    @Test
    fun `invoke returns list of sources from repository`() = runBlocking {
        // Given
        val sources = listOf(
            NewsSource("id1", "name1", "desc1", "url1", "cat1", "lang1", "country1"),
            NewsSource("id2", "name2", "desc2", "url2", "cat2", "lang2", "country2")
        )
        coEvery { sourceRepository.getSources() } returns sources

        // When
        val result = getSourcesUseCase().first()

        // Then
        assertEquals(sources, result)
    }
}
