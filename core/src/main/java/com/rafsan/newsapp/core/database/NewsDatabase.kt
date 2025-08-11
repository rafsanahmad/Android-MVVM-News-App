package com.rafsan.newsapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rafsan.newsapp.core.database.entity.NewsArticleEntity

@Database(
    entities = [NewsArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}