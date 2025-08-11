/*
 * *
 *  * Created by Rafsan Ahmad on 9/27/21, 5:30 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.model.NewsResponse
import com.rafsan.newsapp.domain.model.Source
import com.rafsan.newsapp.state.NetworkState

object FakeDataUtil {
    fun getFakeNewsArticleResponse(): NetworkState<NewsResponse> {
        val articles = getFakeArticles()
        val newsResponse = NewsResponse(
            status = "200", totalResults = 2, articles = articles
        )
        return NetworkState.Success(newsResponse)
    }

    fun getFakeNewsArticleLiveData(): LiveData<List<NewsArticle>> {
        val list = MutableLiveData<List<NewsArticle>>()
        val result: LiveData<List<NewsArticle>> = list
        list.postValue(getFakeArticles())
        return result
    }

    fun getFakeArticles(): MutableList<NewsArticle> {
        val articleList: MutableList<NewsArticle> = arrayListOf()
        val source1 = Source(
            id = "1", name = "BBC"
        )
        val article1 = NewsArticle(
            id = 1, author = "A", content = "ABC", description = "Desc1", publishedAt = "",
            source = source1, title = "Title1", url = "https://google.com", urlToImage = ""
        )
        val source2 = Source(
            id = "2", name = "CNN"
        )
        val article2 = NewsArticle(
            id = 2, author = "B", content = "DEF", description = "Desc2", publishedAt = "",
            source = source2, title = "Title2", url = "https://youtube.com", urlToImage = ""
        )

        articleList.add(article1)
        articleList.add(article2)
        return articleList
    }

    fun getFakeArticle(): NewsArticle {
        val source1 = Source(
            id = "1", name = "BBC"
        )
        val article1 = NewsArticle(
            id = 1, author = "A", content = "ABC", description = "Desc1", publishedAt = "",
            source = source1, title = "Title1", url = "https://google.com", urlToImage = ""
        )
        return article1
    }
}