/*
 * *
 *  * Created by Rafsan Ahmad on 8/13/25, 7:44PM
 *  * Copyright (c) 2025 . All rights reserved.
 *
 */

package com.rafsan.newsapp.feature.favorite

import com.rafsan.newsapp.domain.model.NewsArticle

// Sealed class for UI State
sealed class FavoritesScreenState {
    object Loading : FavoritesScreenState()
    data class Success(val articles: List<NewsArticle>) : FavoritesScreenState()
    object Empty : FavoritesScreenState()
    data class Error(val message: String) : FavoritesScreenState()
}