package com.rafsan.newsapp.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rafsan.newsapp.data.database.entity.NewsArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(newsArticle: NewsArticleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(newsArticles: List<NewsArticleEntity>)

    @Query("SELECT * FROM news_articles")
    fun getAllNews(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE is_favorite = 1")
    fun getFavoriteNews(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE url = :articleUrl")
    suspend fun getArticleByUrl(articleUrl: String): NewsArticleEntity?

    @Query("SELECT * FROM news_articles WHERE url = :articleUrl AND is_favorite = 1")
    suspend fun getFavoriteArticleByUrl(articleUrl: String): NewsArticleEntity?

    @Query("SELECT * FROM news_articles WHERE is_favorite = 0 ORDER BY id DESC")
    fun feedPagingSource(): PagingSource<Int, NewsArticleEntity>

    @Delete
    suspend fun deleteNews(newsArticle: NewsArticleEntity)

    @Query("DELETE FROM news_articles WHERE is_favorite = 0")
    suspend fun deleteCachedArticles()

    @Query("DELETE FROM news_articles")
    suspend fun deleteAllNews()
}


