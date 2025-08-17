/*
 * *
 *  * Created by Rafsan Ahmad on 8/13/25, 7:37PM
 *  * Copyright (c) 2025 . All rights reserved.
 *
 */

package com.rafsan.newsapp.feature.details

import androidx.annotation.StringRes
import com.rafsan.newsapp.domain.model.NewsArticle

// Sealed class for Details Screen UI State
sealed class DetailScreenState {
    object Loading : DetailScreenState()
    data class Success(val article: NewsArticle) : DetailScreenState()
    data class Error(val message: String) : DetailScreenState()
}

sealed class DetailsViewEffect {
    data class ShowSnackbar(@StringRes val message: Int) : DetailsViewEffect()
}