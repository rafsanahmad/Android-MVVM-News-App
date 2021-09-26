package com.rafsan.newsapp.util

import com.rafsan.newsapp.data.model.NewsArticle
import com.rafsan.newsapp.data.model.Source
import com.rafsan.newsapp.di.CoroutinesDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.emptyList

object TestUtil {
    fun getFakeNewsArticleDataList(): List<NewsArticle> {
        val articles = getFakeArticles()
        return articles
    }

    fun getFakeArticles(): List<NewsArticle> {
        val articleList = ArrayList<NewsArticle>(2)
        val source1 = Source(
            id = 1, name = "BBC"
        )
        val article1 = NewsArticle(
            id = 1, author = "A", content = "ABC", description = "Desc1", publishedAt = "2020-8-20",
            source = source1, title = "Title1", url = "https://google.com", urlToImage = ""
        )
        val source2 = Source(
            id = 2, name = "CNN"
        )
        val article2 = NewsArticle(
            id = 2, author = "B", content = "DEF", description = "Desc2", publishedAt = "2021-8-20",
            source = source2, title = "Title2", url = "https://youtube.com", urlToImage = ""
        )

        articleList.add(article1)
        articleList.add(article2)
        return articleList
    }

    @ExperimentalCoroutinesApi
    fun provideFakeCoroutinesDispatcherProvider(
        dispatcher: TestCoroutineDispatcher?
    ): CoroutinesDispatcherProvider {
        val sharedTestCoroutineDispatcher = TestCoroutineDispatcher()
        return CoroutinesDispatcherProvider(
            dispatcher ?: sharedTestCoroutineDispatcher,
            dispatcher ?: sharedTestCoroutineDispatcher,
            dispatcher ?: sharedTestCoroutineDispatcher
        )
    }
}