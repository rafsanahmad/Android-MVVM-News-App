package com.rafsan.newsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rafsan.newsapp.core.navigation.Screen
import com.rafsan.newsapp.feature.details.DetailsRoute
import com.rafsan.newsapp.feature.favorite.FavoritesRoute
import com.rafsan.newsapp.feature.news.FeedRoute
import com.rafsan.newsapp.feature.search.SearchScreen

@Composable
fun SetupNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route,
        modifier = modifier
    ) {
        composable(Screen.Feed.route) {
            FeedRoute(navController = navController)
        }
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }
        composable(Screen.Favorites.route) {
            FavoritesRoute(navController = navController)
        }
        composable(Screen.Details.route) {
            DetailsRoute(navController = navController)
        }
    }
}