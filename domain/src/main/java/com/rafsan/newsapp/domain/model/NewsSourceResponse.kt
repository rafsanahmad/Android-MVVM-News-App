package com.rafsan.newsapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NewsSourceResponse(
    val status: String,
    val sources: List<NewsSource>
)
