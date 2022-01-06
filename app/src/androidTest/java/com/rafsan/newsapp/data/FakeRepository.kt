/*
 * *
 *  * Created by Rafsan Ahmad on 11/29/21, 4:42 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

package com.rafsan.newsapp.data

import FakeDataUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rafsan.newsapp.data.model.NewsArticle
import com.rafsan.newsapp.data.model.NewsResponse
import com.rafsan.newsapp.network.repository.INewsRepository
import com.rafsan.newsapp.state.NetworkState

class FakeRepository : INewsRepository {

    private val observableNewsArticle = MutableLiveData<List<NewsArticle>>()

    override suspend fun getNews(
        countryCode: String,
        pageNumber: Int
    ): NetworkState<NewsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun searchNews(
        searchQuery: String,
        pageNumber: Int
    ): NetworkState<NewsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun saveNews(news: NewsArticle): Long {
        TODO("Not yet implemented")
    }

    override fun getSavedNews(): LiveData<List<NewsArticle>> {
        return FakeDataUtil.getFakeNewsArticleLiveData()
    }

    override suspend fun deleteNews(news: NewsArticle) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllNews() {
        TODO("Not yet implemented")
    }
}