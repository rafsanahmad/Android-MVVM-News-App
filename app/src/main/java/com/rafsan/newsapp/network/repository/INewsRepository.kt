/*
 * *
 *  * Created by Rafsan Ahmad on 11/29/21, 4:41 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

package com.rafsan.newsapp.network.repository

import androidx.lifecycle.LiveData
import com.rafsan.newsapp.data.model.NewsArticle
import com.rafsan.newsapp.data.model.NewsResponse
import com.rafsan.newsapp.state.NetworkState

interface INewsRepository {
    suspend fun getNews(countryCode: String, pageNumber: Int): NetworkState<NewsResponse>

    suspend fun searchNews(searchQuery: String, pageNumber: Int): NetworkState<NewsResponse>

    suspend fun saveNews(news: NewsArticle): Long

    fun getSavedNews(): LiveData<List<NewsArticle>>

    suspend fun deleteNews(news: NewsArticle)

    suspend fun deleteAllNews()
}
