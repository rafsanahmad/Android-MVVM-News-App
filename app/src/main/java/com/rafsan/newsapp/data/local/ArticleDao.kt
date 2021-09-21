package com.rafsan.newsapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rafsan.newsapp.data.model.NewsArticle

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(newsArticle: NewsArticle): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<NewsArticle>>

    @Delete
    suspend fun deleteArticle(newsArticle: NewsArticle)
}