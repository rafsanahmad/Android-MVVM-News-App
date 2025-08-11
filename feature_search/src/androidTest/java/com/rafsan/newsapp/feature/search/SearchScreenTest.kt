package com.rafsan.newsapp.feature.search

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.navigation.testing.TestNavHostController
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun search_field_accepts_input() {
        composeRule.setContent {
            val navController = TestNavHostController(composeRule.activity)
            // Mock SearchViewModel would need test DI setup; this is a smoke test
            SearchRoute(navController)
        }
        
        val searchField = composeRule.onNodeWithText("Search")
        searchField.assertIsDisplayed()
        searchField.performTextInput("android")
    }
}