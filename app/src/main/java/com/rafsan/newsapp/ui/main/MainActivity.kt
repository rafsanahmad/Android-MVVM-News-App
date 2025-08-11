/*
 * *
 *  * Created by Rafsan Ahmad on 9/27/21, 5:30 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

package com.rafsan.newsapp.ui.main

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
        composable(Screen.Feed.route) { com.rafsan.newsapp.feature.news.FeedRoute(navController) }
        composable(Screen.Search.route) { com.rafsan.newsapp.feature.search.SearchRoute(navController) }
        composable(Screen.Favorites.route) { com.rafsan.newsapp.feature.favorite.FavoritesRoute(navController) }
        composable(Screen.Details.route) { com.rafsan.newsapp.feature.details.DetailsRoute(navController) }
    }
}