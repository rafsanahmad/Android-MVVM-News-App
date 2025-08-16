package com.rafsan.newsapp.feature.search

// Import R from the feature module explicitly
import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.paging.PagingData
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.model.Source
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SearchScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController
    private lateinit var mockViewModel: SearchViewModel

    // Dummy data
    private val dummyArticle1 = NewsArticle(
        id = 1,
        author = "Author 1",
        content = "Content 1",
        description = "Description 1",
        publishedAt = "2023-01-01T00:00:00Z",
        source = Source(id = "src1", name = "Source 1"),
        title = "Unique Article Title 1 for Test",
        url = "http://example.com/1",
        urlToImage = "http://example.com/img1.jpg"
    )
    private val dummyArticle2 = NewsArticle(
        id = 2,
        author = "Author 2",
        content = "Content 2",
        description = "Description 2",
        publishedAt = "2023-01-02T00:00:00Z",
        source = Source(id = "src2", name = "Source 2"),
        title = "Distinct Article Title 2 for Test",
        url = "http://example.com/2",
        urlToImage = "http://example.com/img2.jpg"
    )

    // To hold the mutable states for the mocked ViewModel
    private lateinit var _mockCurrentQuery: MutableStateFlow<String>
    private lateinit var _mockSearchResults: MutableStateFlow<PagingData<NewsArticle>>

    @Before
    fun setUp() {
        hiltRule.inject()

        navController = TestNavHostController(composeTestRule.activity.applicationContext)
        // Optional: Setup NavHost with TestNavHostController for deeper navigation testing
        // composeTestRule.activity.runOnUiThread {
        //     navController.setGraph(R.navigation.your_nav_graph_if_any_for_testing)
        // }

        mockViewModel = mockk(relaxed = true)

        _mockCurrentQuery = MutableStateFlow("")
        _mockSearchResults = MutableStateFlow(PagingData.empty())

        // Stubbing the StateFlows
        every { mockViewModel.currentQuery } returns _mockCurrentQuery.asStateFlow()
        every { mockViewModel.searchResults } returns _mockSearchResults.asStateFlow()

        // Stubbing onQueryChanged to update our local StateFlows
        every { mockViewModel.onQueryChanged(any()) } answers {
            val newQuery = firstArg<String>()
            _mockCurrentQuery.value = newQuery
            if (newQuery.isNotBlank()) {
                if (newQuery.equals("empty_results_query", ignoreCase = true)) {
                    _mockSearchResults.value = PagingData.empty()
                } else if (newQuery.equals("error_query", ignoreCase = true)) {
                    _mockSearchResults.value = PagingData.empty() // Simplified error sim
                } else {
                    _mockSearchResults.value = PagingData.from(listOf(dummyArticle1, dummyArticle2))
                }
            } else {
                _mockSearchResults.value = PagingData.empty()
            }
        }

        composeTestRule.setContent {
            SearchScreen(navController = navController, viewModel = mockViewModel)
        }
    }

    @Test
    fun searchScreen_displaysSearchBarAndInitialMessage() {
        composeTestRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.HintText,
                "Search news…"
            ), // Using SemanticsMatcher
            useUnmergedTree = true
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.start_typing_to_search))
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_typingInSearchBarUpdatesQueryAndDisplaysResults() {
        val testQuery = "test"
        composeTestRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.HintText,
                "Search news…"
            ), // Using SemanticsMatcher
            useUnmergedTree = true
        ).performTextInput(testQuery)

        verify { mockViewModel.onQueryChanged(testQuery) }

        composeTestRule.onNodeWithText(dummyArticle1.title!!).assertIsDisplayed()
        composeTestRule.onNodeWithText(dummyArticle2.title!!).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.start_typing_to_search))
            .assertDoesNotExist()
    }

    @Test
    fun searchScreen_clearQueryButtonWorks() {
        val testQuery = "test"
        composeTestRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.HintText,
                "Search news…"
            ), // Using SemanticsMatcher
            useUnmergedTree = true
        ).performTextInput(testQuery)
        verify { mockViewModel.onQueryChanged(testQuery) }
        composeTestRule.onNodeWithText(dummyArticle1.title!!).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.cd_clear_search))
            .assertIsDisplayed()
            .performClick()

        verify { mockViewModel.onQueryChanged("") } // Verify query is cleared

        // Check if the text field is empty by checking its text (not hint)
        composeTestRule.onNode(
            SemanticsMatcher.expectValue(SemanticsProperties.HintText, "Search news…"),
            useUnmergedTree = true
        ).assertTextEquals("") // Assumes the text itself is empty, hint is still there

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.start_typing_to_search))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(dummyArticle1.title!!).assertDoesNotExist()
    }

    @Test
    fun searchScreen_displaysNoResultsMessageWhenApplicable() {
        val queryWithNoResults = "empty_results_query"
        composeTestRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.HintText,
                "Search news…"
            ), // Using SemanticsMatcher
            useUnmergedTree = true
        ).performTextInput(queryWithNoResults)

        verify { mockViewModel.onQueryChanged(queryWithNoResults) }

        val expectedMessage = composeTestRule.activity.getString(
            R.string.no_results_found_for_query,
            queryWithNoResults
        )
        composeTestRule.onNodeWithText(expectedMessage).assertIsDisplayed()
    }

    @Test
    fun searchScreen_itemClickNavigatesToDetails() {
        // Prime the ViewModel to have results for a query
        _mockCurrentQuery.value = "dummyQuery" // Set query
        _mockSearchResults.value = PagingData.from(listOf(dummyArticle1)) // Set results

        // Wait for UI to recompose with new state
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(dummyArticle1.title!!)
            .assertIsDisplayed()
            .performClick()

        // If TestNavHostController is set up with a NavGraph, you can assert current destination.
        // For example:
        // assertEquals(Screen.Details.route, navController.currentBackStackEntry?.destination?.route)
        // This requires `navController.setGraph` in `setUp` and appropriate NavGraph.
        // For now, this confirms clickability. Further navigation testing requires NavHost setup.
        // For example, to verify the navigate was called (if navController was a mockk object):
        // verify { navController.navigate(Screen.Details.route) } // or a matcher for the route with args
    }

    @Test
    fun searchScreen_backButtonInAppBarNavigatesBack() {
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.cd_navigate_back))
            .performClick()
        // Example with TestNavHostController: verify that popBackStack was effectively called
        // or that the current destination changed. This requires initial setup of the NavController's
        // backstack or graph to test meaningful back navigation.
        // verify { navController.popBackStack() } // If navController was mockk
    }
}
