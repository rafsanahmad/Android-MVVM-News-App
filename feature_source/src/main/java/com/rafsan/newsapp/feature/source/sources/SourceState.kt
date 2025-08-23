/*
 * *
 *  * Created by Rafsan Ahmad on 8/23/25, 4:11PM
 *  * Copyright (c) 2025 . All rights reserved.
 *
 */

package com.rafsan.newsapp.feature.source.sources

import com.rafsan.newsapp.domain.model.NewsSource

sealed class SourceState {
    data object Loading : SourceState()
    data class Success(val sources: List<NewsSource>) : SourceState()
    data class Error(val message: String) : SourceState()
}