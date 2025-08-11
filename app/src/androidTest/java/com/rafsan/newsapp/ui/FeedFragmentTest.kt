/*
 * *
 *  * Created by Rafsan Ahmad on 11/28/21, 8:35 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

package com.rafsan.newsapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.rafsan.newsapp.feature.news.FeedScreen
import org.junit.Rule
import org.junit.Test

class FeedUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun feed_shows_loading_and_items() {
        composeRule.setContent {
            val paging = androidx.paging.compose.collectAsLazyPagingItems(
                kotlinx.coroutines.flow.flowOf(
                    androidx.paging.PagingData.from(
                        listOf(
                            com.rafsan.newsapp.domain.model.NewsArticle(
                                1, "A", "", "Desc1", "", null, "Title1", "", "")
                        )
                    )
                )
            )
            FeedScreen(state = paging, onClick = {})
        }
        composeRule.onNodeWithText("Title1").assertIsDisplayed()
    }
}