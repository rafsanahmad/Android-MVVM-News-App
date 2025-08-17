package com.rafsan.newsapp.domain.model

data class NewsResponse(
    val status: String?,
    val totalResults: Int,
    val articles: MutableList<NewsArticle>
)