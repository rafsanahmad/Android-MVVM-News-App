package com.rafsan.newsapp.domain.model

import android.annotation.SuppressLint
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class NewsSource(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val category: String,
    val language: String,
    val country: String
)
