/*
 * *
 *  * Created by Rafsan Ahmad on 9/27/21, 5:30 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

package com.rafsan.newsapp.favorites

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.rafsan.newsapp.data.local.NewsDao
import com.rafsan.newsapp.data.local.NewsDatabase
import com.rafsan.newsapp.data.model.NewsArticle
import com.rafsan.newsapp.network.api.ApiHelper
import com.rafsan.newsapp.network.repository.NewsRepository
import com.rafsan.newsapp.util.MainCoroutineRule
import com.rafsan.newsapp.util.MockWebServerBaseTest
import com.rafsan.newsapp.util.TestUtil
import com.rafsan.newsapp.util.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class NewsRepositoryTest : MockWebServerBaseTest() {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private lateinit var newsRepository: NewsRepository
    private lateinit var newsDatabase: NewsDatabase
    private lateinit var newsDao: NewsDao
    private lateinit var apiHelper: ApiHelper
    private lateinit var responseObserver: Observer<List<NewsArticle>>

    override fun isMockServerEnabled(): Boolean = true

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        newsDatabase = Room.inMemoryDatabaseBuilder(
            context, NewsDatabase::class.java
        ).allowMainThreadQueries().build()
        newsDao = newsDatabase.getNewsDao()
        apiHelper = provideTestApiService()
        newsRepository = NewsRepository(apiHelper, newsDao)
        responseObserver = Observer { }
    }

    @Test
    fun testFavoriteNewsInsertionInDb() {
        coroutineRule.runBlockingTest {
            newsRepository.saveNews(TestUtil.getFakeArticle())
            val favNews = newsRepository.getSavedNews()
            favNews.observeForever(responseObserver)
            assertThat(favNews.value?.isNotEmpty()).isTrue()
        }
    }

    @Test
    fun testRemoveFromDb() {
        coroutineRule.runBlockingTest {
            newsRepository.deleteAllNews()
            val favNews = newsRepository.getSavedNews()
            favNews.observeForever(responseObserver)
            assertThat(favNews.value?.isEmpty()).isTrue()
        }
    }

    @Test
    fun testFavoriteNews() {
        coroutineRule.runBlockingTest {
            val fakeArticle = TestUtil.getFakeArticle()
            newsRepository.saveNews(fakeArticle)
            val favoriteArticle = newsRepository.getSavedNews()
            favoriteArticle.observeForever(responseObserver)
            assertThat(favoriteArticle.value?.get(0)?.id == fakeArticle.id).isTrue()
        }
    }

    @After
    fun release() {
        newsDatabase.close()
        newsRepository.getSavedNews().removeObserver(responseObserver)
    }
}