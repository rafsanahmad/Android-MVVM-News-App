package com.rafsan.newsapp.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class NewsSourceResponse(
    val status: String,
    val sources: List<NewsSource>
)
