package com.rafsan.newsapp.domain.model

import android.annotation.SuppressLint
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class NewsSourceResponse(
    val status: String,
    val sources: List<NewsSource>
)
