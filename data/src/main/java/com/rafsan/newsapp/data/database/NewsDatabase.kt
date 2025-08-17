package com.rafsan.newsapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rafsan.newsapp.data.database.entity.NewsArticleEntity

@Database(
    entities = [NewsArticleEntity::class],
    version = 3,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_news_articles_url ON news_articles(url)")
    }
}

