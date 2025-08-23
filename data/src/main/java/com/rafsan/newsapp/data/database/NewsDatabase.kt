package com.rafsan.newsapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rafsan.newsapp.data.database.entity.NewsArticleEntity
import com.rafsan.newsapp.data.database.entity.SourceEntity

@Database(
    entities = [NewsArticleEntity::class, SourceEntity::class],
    version = 4,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun sourceDao(): SourceDao
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE news_articles ADD COLUMN favorited_at INTEGER")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE `sources` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `url` TEXT NOT NULL, `category` TEXT NOT NULL, `language` TEXT NOT NULL, `country` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))"
        )
    }
}
