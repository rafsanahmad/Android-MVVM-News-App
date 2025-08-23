package com.rafsan.newsapp.data.network

import com.rafsan.newsapp.domain.model.NewsResponse
import com.rafsan.newsapp.domain.model.NewsSourceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/top-headlines")
    suspend fun getNews(
        @Query("country") countryCode: String,
        @Query("page") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") searchQuery: String,
        @Query("page") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String,
        @Query("sources") sources: String? = null
    ): Response<NewsResponse>

    @GET("v2/top-headlines/sources")
    suspend fun getSources(
        @Query("apiKey") apiKey: String
    ): Response<NewsSourceResponse>
}


