package com.rafsan.newsapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

import androidx.room.ColumnInfo

@Entity(tableName = "news_articles")
data class NewsArticleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val sourceId: String?,
    val sourceName: String?,
    val title: String?,
    val url: String?,
    val urlToImage: String?,
    @ColumnInfo(name = "is_favorite", defaultValue = "0")
    val isFavorite: Boolean = false,
    val favoritedAt: Long? = null
)


