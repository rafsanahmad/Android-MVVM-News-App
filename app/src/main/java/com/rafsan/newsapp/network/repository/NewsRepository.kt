/*
 * *
 *  * Created by Rafsan Ahmad on 9/27/21, 5:30 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

package com.rafsan.newsapp.network.repository

import com.rafsan.newsapp.data.local.NewsDao
import com.rafsan.newsapp.data.model.NewsArticle
import com.rafsan.newsapp.data.model.NewsResponse
import com.rafsan.newsapp.network.api.ApiHelper
import com.rafsan.newsapp.utils.NetworkResult
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val remoteDataSource: ApiHelper,
    private val localDataSource: NewsDao
) : INewsRepository {

    override suspend fun getNews(
        countryCode: String,
        pageNumber: Int
    ): NetworkResult<NewsResponse> {
        return try {
            val response = remoteDataSource.getNews(countryCode, pageNumber)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                NetworkResult.Success(result)
            } else {
                NetworkResult.Error("An error occurred")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Error occurred ${e.localizedMessage}")
        }
    }

    override suspend fun searchNews(
        searchQuery: String,
        pageNumber: Int
    ): NetworkResult<NewsResponse> {
        return try {
            val response = remoteDataSource.searchNews(searchQuery, pageNumber)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                NetworkResult.Success(result)
            } else {
                NetworkResult.Error("An error occurred")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Error occurred ${e.localizedMessage}")
        }
    }

    override suspend fun saveNews(news: NewsArticle) = localDataSource.upsert(news)

    override fun getSavedNews() = localDataSource.getAllNews()

    override suspend fun deleteNews(news: NewsArticle) = localDataSource.deleteNews(news)

    override suspend fun deleteAllNews() = localDataSource.deleteAllNews()
}