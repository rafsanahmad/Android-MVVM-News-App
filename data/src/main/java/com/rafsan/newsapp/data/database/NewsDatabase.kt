package com.rafsan.newsapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rafsan.newsapp.data.database.entity.NewsArticleEntity

@Database(
    entities = [NewsArticleEntity::class],
    version = 2,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}


