package com.rafsan.newsapp.domain.model

import kotlinx.serialization.Serializable

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
