package com.rafsan.newsapp.core.navigation

sealed class Screen(val route: String) {
    data object Feed : Screen("feed")
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object Details : Screen("details")
}