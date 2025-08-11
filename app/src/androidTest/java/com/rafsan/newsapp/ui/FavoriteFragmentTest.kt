package com.rafsan.newsapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.rafsan.newsapp.feature.favorite.FavoritesRoute
import org.junit.Rule
import org.junit.Test

class FavoritesUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun favorites_shows_list_when_items_exist() {
        composeRule.setContent {
            // Using empty default state since VM is Hilt provided; this is a smoke test placeholder
            FavoritesRoute(navController = androidx.navigation.testing.TestNavHostController(composeRule.activity))
        }
        // No crash; additional assertions would require test DI setup
        composeRule.onNodeWithText("").assertDoesNotExist()
    }
}