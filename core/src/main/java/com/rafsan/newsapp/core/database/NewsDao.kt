package com.rafsan.newsapp.core.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rafsan.newsapp.core.database.entity.NewsArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(newsArticle: NewsArticleEntity): Long

    @Query("SELECT * FROM news_articles")
    fun getAllNews(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE url = :articleUrl") // New method
    suspend fun getArticleByUrl(articleUrl: String): NewsArticleEntity?

    @Query("SELECT * FROM news_articles ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, NewsArticleEntity>

    @Delete
    suspend fun deleteNews(newsArticle: NewsArticleEntity)

    @Query("DELETE FROM news_articles")
    suspend fun deleteAllNews()
}