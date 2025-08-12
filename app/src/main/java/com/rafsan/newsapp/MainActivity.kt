/*
 * *
 *  * Created by Rafsan Ahmad on 8/12/25, 12:01AM
 *  * Copyright (c) 2025 . All rights reserved.
 *  *
 */

package com.rafsan.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rafsan.newsapp.core.navigation.Screen
import com.rafsan.newsapp.feature.details.DetailsRoute
import com.rafsan.newsapp.feature.favorite.FavoritesRoute
import com.rafsan.newsapp.feature.news.FeedRoute
import com.rafsan.newsapp.feature.search.SearchRoute
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.padding

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppScaffold()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val showBars = when (currentDestination?.route) {
        Screen.Feed.route, Screen.Favorites.route, Screen.Search.route -> true
        else -> false
    }

    Scaffold(
        topBar = {
            if (showBars) {
                TopAppBar(
                    title = {
                        val titleRes = when (currentDestination?.route) {
                            Screen.Feed.route -> R.string.today_news
                            Screen.Favorites.route -> R.string.favorite_news
                            Screen.Search.route -> R.string.search
                            else -> R.string.app_name
                        }
                        Text(text = stringResource(id = titleRes))
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = stringResource(id = R.string.search))
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBars) {
                NavigationBar {
                    val items = listOf(
                        Screen.Feed to Icons.Default.Home,
                        Screen.Favorites to Icons.Default.Favorite
                    )
                    items.forEach { (screen, icon) ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Feed.route) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            icon = { Icon(imageVector = icon, contentDescription = stringResource(id = if (screen == Screen.Feed) R.string.feed else R.string.favorites)) },
                            label = { Text(text = stringResource(id = if (screen == Screen.Feed) R.string.feed else R.string.favorites)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = Screen.Feed.route, modifier = Modifier.padding(innerPadding)) {
            composable(Screen.Feed.route) { FeedRoute(navController) }
            composable(Screen.Search.route) { SearchRoute(navController) }
            composable(Screen.Favorites.route) { FavoritesRoute(navController) }
            composable(Screen.Details.route) { DetailsRoute(navController) }
        }
    }
}