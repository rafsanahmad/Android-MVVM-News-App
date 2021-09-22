package com.rafsan.newsapp.network.repository

import com.rafsan.newsapp.data.local.NewsDao
import com.rafsan.newsapp.data.model.NewsArticle
import com.rafsan.newsapp.network.api.ApiHelper
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val remoteDataSource: ApiHelper,
    private val localDataSource: NewsDao
) {

    suspend fun getNews(countryCode: String, pageNumber: Int) =
        remoteDataSource.getNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        remoteDataSource.searchNews(searchQuery, pageNumber)

    suspend fun upsert(news: NewsArticle) = localDataSource.upsert(news)

    fun getSavedNews() = localDataSource.getAllNews()

    suspend fun deleteNews(news: NewsArticle) = localDataSource.deleteNews(news)
}