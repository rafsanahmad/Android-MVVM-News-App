package com.rafsan.newsapp.feature.news

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rafsan.newsapp.domain.model.NewsArticle
import kotlinx.coroutines.flow.flowOf
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun feedScreen_displaysArticleTitleAndDescription() {
        val article = NewsArticle(
            id = null,
            author = "Author",
            content = "Content",
            description = "Description",
            publishedAt = null,
            source = null,
            title = "Title",
            url = "https://example.com",
            urlToImage = null
        )

        composeRule.setContent {
            val items = flowOf(PagingData.from(listOf(article))).collectAsLazyPagingItems()
            FeedScreen(state = items, onClick = {})
        }

        composeRule.onNodeWithText("Title").assertIsDisplayed()
        composeRule.onNodeWithText("Description").assertIsDisplayed()
    }
}