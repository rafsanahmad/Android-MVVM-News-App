package com.rafsan.newsapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rafsan.newsapp.data.model.NewsArticle

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(newsArticle: NewsArticle): Long

    @Query("SELECT * FROM articles")
    fun getAllNews(): LiveData<List<NewsArticle>>

    @Delete
    suspend fun deleteNews(newsArticle: NewsArticle)
}