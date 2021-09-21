package com.rafsan.newsapp.data.model


data class NewsResponse(
    val newsArticles: MutableList<NewsArticle>,
    val status: String,
    val totalResults: Int
)