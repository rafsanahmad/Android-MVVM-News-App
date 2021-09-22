package com.rafsan.newsapp.network.api

import com.rafsan.newsapp.data.model.NewsResponse
import retrofit2.Response

interface ApiHelper {

    suspend fun searchNews(query: String, pageNumber: Int): Response<NewsResponse>
    suspend fun getNews(countryCode: String, pageNumber: Int): Response<NewsResponse>
}