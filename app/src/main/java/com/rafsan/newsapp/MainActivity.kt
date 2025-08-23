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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rafsan.newsapp.core.navigation.Screen
import com.rafsan.newsapp.ui.navigation.SetupNavGraph
import dagger.hilt.android.AndroidEntryPoint

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

    val showMainTopAppBar = when (currentDestination?.route) {
        Screen.Feed.route, Screen.Favorites.route, Screen.Source.route -> true
        else -> false // Hide for Search (has its own), Details, etc.
    }
    val showBottomNavBar = when (currentDestination?.route) {
        Screen.Feed.route, Screen.Favorites.route, Screen.Source.route -> true // Show for main tabs
        else -> false
    }

    Scaffold(topBar = {
        if (showMainTopAppBar) { // Conditional TopAppBar
            TopAppBar(title = {
                val titleRes = when (currentDestination?.route) {
                    Screen.Feed.route -> R.string.today_news
                    Screen.Favorites.route -> R.string.favorite_news
                    Screen.Source.route -> R.string.sources
                    else -> R.string.app_name
                }
                Text(text = stringResource(id = titleRes))
            }, actions = {
                // Show search icon only on screens where it makes sense (e.g., Feed, Favorites)
                if (currentDestination?.route == Screen.Feed.route || currentDestination?.route == Screen.Favorites.route) {
                    IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search)
                        )
                    }
                }
            })
        }
    }, bottomBar = {
        if (showBottomNavBar) { // Conditional BottomNavigationBar
            NavigationBar {
                val items = listOf(
                    Screen.Feed to Icons.Default.Home,
                    Screen.Favorites to Icons.Default.Favorite,
                    Screen.Source to Icons.Default.List
                )
                items.forEach { (screen, icon) ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(screen.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = stringResource(id = R.string.sources)
                            )
                        },
                        label = {
                            val labelRes = when (screen) {
                                Screen.Feed -> R.string.feed
                                Screen.Favorites -> R.string.favorites
                                Screen.Source -> R.string.sources
                                else -> R.string.app_name
                            }
                            Text(text = stringResource(id = labelRes))
                        })
                }
            }
        }
    }) { innerPadding ->
        // Use the new SetupNavGraph function
        SetupNavGraph(
            navController = navController, modifier = Modifier.padding(innerPadding)
        )
    }
}