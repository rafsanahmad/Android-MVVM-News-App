package com.rafsan.newsapp.core.navigation

import com.rafsan.newsapp.domain.model.NewsArticle
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class Screen(val route: String) {
    data object Feed : Screen("feed")
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object Details : Screen("details/{article}") {
        fun withArticle(article: NewsArticle): String {
            val articleJson = Json.encodeToString(article)
            return "details/$articleJson"
        }
    }
}