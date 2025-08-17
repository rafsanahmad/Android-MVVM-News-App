package com.rafsan.newsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rafsan.newsapp.core.navigation.Screen
import com.rafsan.newsapp.feature.details.DetailsScreen
import com.rafsan.newsapp.feature.favorite.FavoritesScreen
import com.rafsan.newsapp.feature.news.FeedScreen
import com.rafsan.newsapp.feature.search.SearchScreen

@Composable
fun SetupNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route,
        modifier = modifier
    ) {
        composable(Screen.Feed.route) {
            FeedScreen(navController = navController)
        }
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController = navController)
        }
        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("article") { type = NavType.StringType })
        ) {
            DetailsScreen(navController = navController)
        }
    }
}