package com.rafsan.newsapp.feature.favorite

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.navigation.testing.TestNavHostController
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun favorites_screen_renders() {
        composeRule.setContent {
            val navController = TestNavHostController(composeRule.activity)
            // Mock FavoritesViewModel would need test DI setup; this is a smoke test
            FavoritesRoute(navController)
        }
        // Test passes if no crash occurs during composition
    }
}