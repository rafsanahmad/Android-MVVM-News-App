/*
 * *
 *  * Created by Rafsan Ahmad on 11/28/21, 10:05 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

package com.rafsan.newsapp.ui

import FakeDataUtil
import MainCoroutineRule
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavDeepLinkBuilder
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.mockitokotlin2.whenever
import com.rafsan.newsapp.R
import com.rafsan.newsapp.data.FakeRepository
import com.rafsan.newsapp.network.api.NewsApi
import com.rafsan.newsapp.ui.main.MainActivity
import com.rafsan.newsapp.ui.main.MainViewModel
import com.rafsan.newsapp.utils.NetworkHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import provideFakeCoroutinesDispatcherProvider
import runBlockingTest


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FavoriteFragmentTest {
    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var newsApi: NewsApi
    private lateinit var networkHelper: NetworkHelper
    private lateinit var fakeRepository: FakeRepository
    lateinit var instrumentationContext: Context

    private val testDispatcher = coroutineRule.testDispatcher

    private val itemInTest = 0
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        networkHelper = mock(NetworkHelper::class.java)
        newsApi = mock(NewsApi::class.java)
        fakeRepository = mock(FakeRepository::class.java)
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
        viewModel = MainViewModel(
            repository = fakeRepository,
            networkHelper = networkHelper,
            coroutinesDispatcherProvider = provideFakeCoroutinesDispatcherProvider(testDispatcher)
        )
    }

    protected fun launchFragment(
        destinationId: Int,
        argBundle: Bundle? = null
    ) {
        val launchFragmentIntent = buildLaunchFragmentIntent(destinationId, argBundle)
        launchActivity<MainActivity>(launchFragmentIntent)
    }

    private fun buildLaunchFragmentIntent(destinationId: Int, argBundle: Bundle?): Intent =
        NavDeepLinkBuilder(InstrumentationRegistry.getInstrumentation().targetContext)
            .setGraph(R.navigation.nav_graph)
            .setComponentName(MainActivity::class.java)
            .setDestination(destinationId)
            .setArguments(argBundle)
            .createTaskStackBuilder().intents[0]

    @Test
    fun test_isFavoriteItemsVisibleOnFavouriteTab() {
        launchFragment(R.id.favoriteFragment, null)
        coroutineRule.runBlockingTest {
            whenever(networkHelper.isNetworkConnected())
                .thenReturn(false)
            whenever(fakeRepository.getSavedNews())
                .thenReturn(FakeDataUtil.getFakeNewsArticleLiveData())

            //When
            viewModel.getFavoriteNews()

            Espresso.onView(ViewMatchers.withId(R.id.rvFavoriteNews))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun test_selectFavoriteItem_isDetailFragmentVisible() {
        launchFragment(R.id.favoriteFragment, null)
        coroutineRule.runBlockingTest {
            whenever(networkHelper.isNetworkConnected())
                .thenReturn(false)
            whenever(fakeRepository.getSavedNews())
                .thenReturn(FakeDataUtil.getFakeNewsArticleLiveData())

            //When
            viewModel.getFavoriteNews()
            Espresso.onView(ViewMatchers.withId(R.id.rvFavoriteNews))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        itemInTest,
                        ViewActions.click()
                    )
                )

            // Confirm nav to DetailFragment
            Espresso.onView(ViewMatchers.withId(R.id.webView))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @After
    fun cleanup() {

    }
}