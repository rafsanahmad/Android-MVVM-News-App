/*
 * *
 *  * Created by Rafsan Ahmad on 8/12/25, 12:01 AM
 *  * Copyright (c) 2025 . All rights reserved.
 *
 */

package com.rafsan.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rafsan.newsapp.core.navigation.Screen
import com.rafsan.newsapp.feature.details.DetailsRoute
import com.rafsan.newsapp.feature.favorite.FavoritesRoute
import com.rafsan.newsapp.feature.news.FeedRoute
import com.rafsan.newsapp.feature.search.SearchRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
private fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Feed.route) {
        composable(Screen.Feed.route) { FeedRoute(navController) }
        composable(Screen.Search.route) { SearchRoute(navController) }
        composable(Screen.Favorites.route) { FavoritesRoute(navController) }
        composable(Screen.Details.route) { DetailsRoute(navController) }
    }
}