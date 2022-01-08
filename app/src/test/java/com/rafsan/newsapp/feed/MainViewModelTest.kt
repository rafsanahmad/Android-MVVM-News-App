/*
 * *
 *  * Created by Rafsan Ahmad on 9/27/21, 5:30 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

package com.rafsan.newsapp.feed

import FakeDataUtil
import MainCoroutineRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.whenever
import com.rafsan.newsapp.network.api.NewsApi
import com.rafsan.newsapp.network.repository.NewsRepository
import com.rafsan.newsapp.state.NetworkState
import com.rafsan.newsapp.ui.main.MainViewModel
import com.rafsan.newsapp.utils.Constants.Companion.CountryCode
import com.rafsan.newsapp.utils.NetworkHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import provideFakeCoroutinesDispatcherProvider
import runBlockingTest

@ExperimentalCoroutinesApi
class MainViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var newsApi: NewsApi

    @Mock
    private lateinit var networkHelper: NetworkHelper

    @Mock
    private lateinit var newsRepository: NewsRepository

    private val testDispatcher = coroutineRule.testDispatcher

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(
            repository = newsRepository,
            networkHelper = networkHelper,
            coroutinesDispatcherProvider = provideFakeCoroutinesDispatcherProvider(testDispatcher)
        )
    }

    @Test
    fun `when calling for results then return loading state`() {
        coroutineRule.runBlockingTest {
            whenever(networkHelper.isNetworkConnected())
                .thenReturn(true)
            whenever(newsRepository.getNews(CountryCode, 1))
                .thenReturn(NetworkState.Loading())

            //When
            viewModel.fetchNews(CountryCode)

            //Then
            assertThat(viewModel.newsResponse.value).isNotNull()
            assertThat(viewModel.newsResponse.value.data).isNull()
            assertThat(viewModel.newsResponse.value.message).isNull()
        }
    }

    @Test
    fun `when calling for results then return news results`() {
        coroutineRule.runBlockingTest {
            whenever(networkHelper.isNetworkConnected())
                .thenReturn(true)

            // Stub repository with fake favorites
            whenever(newsRepository.getNews(CountryCode, 1))
                .thenAnswer { (FakeDataUtil.getFakeNewsArticleResponse()) }

            //When
            viewModel.fetchNews(CountryCode)

            //then
            assertThat(viewModel.newsResponse.value).isNotNull()
            val articles = viewModel.newsResponse.value.data?.articles
            assertThat(articles?.isNotEmpty())
            // compare the response with fake list
            assertThat(articles).hasSize(FakeDataUtil.getFakeArticles().size)
            // compare the data and also order
            assertThat(articles).containsExactlyElementsIn(
                FakeDataUtil.getFakeArticles()
            ).inOrder()
        }
    }

    @Test
    fun `when calling for results then return error`() {
        coroutineRule.runBlockingTest {
            whenever(networkHelper.isNetworkConnected())
                .thenReturn(true)
            // Stub repository with fake favorites
            whenever(newsRepository.getNews(CountryCode, 1))
                .thenAnswer { NetworkState.Error("Error occurred", null) }

            //When
            viewModel.fetchNews(CountryCode)

            //then
            val response = viewModel.newsResponse.value
            assertThat(response.message).isNotNull()
            assertThat(response.message).isEqualTo("Error occurred")
        }
    }

    @Test
    fun `when calling for search then return search result`() {
        coroutineRule.runBlockingTest {
            whenever(networkHelper.isNetworkConnected())
                .thenReturn(true)

            // Stub repository with fake favorites
            whenever(newsRepository.searchNews(CountryCode, 1))
                .thenAnswer { (FakeDataUtil.getFakeNewsArticleResponse()) }

            //When
            viewModel.searchNews(CountryCode)

            //then
            assertThat(viewModel.searchNewsResponse.value).isNotNull()
            val articles = viewModel.searchNewsResponse.value.data?.articles
            assertThat(articles?.isNotEmpty())
            // compare the response with fake list
            assertThat(articles).hasSize(FakeDataUtil.getFakeArticles().size)
            // compare the data and also order
            assertThat(articles).containsExactlyElementsIn(
                FakeDataUtil.getFakeArticles()
            ).inOrder()
        }
    }

    @Test
    fun `test format date with T`() {
        val result = viewModel.formatDate("2021-09-29T13:01:31Z")
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("Sep 29, 2021 01:01 PM")
    }

    @Test
    fun `test format date without T`() {
        val result = viewModel.formatDate("2021-09-29 3:01:31 PM")
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("2021-09-29 3:01:31 PM")
    }

    @After
    fun release() {
        Mockito.framework().clearInlineMocks()
    }
}