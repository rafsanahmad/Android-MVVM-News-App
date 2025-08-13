package com.rafsan.newsapp.core.di

import com.rafsan.newsapp.core.BuildConfig
import com.rafsan.newsapp.core.PagingConstants
import com.rafsan.newsapp.core.database.NewsDao
import com.rafsan.newsapp.core.database.NewsDatabase
import com.rafsan.newsapp.core.network.NewsApi
import com.rafsan.newsapp.core.repository.FavoriteRepositoryImpl
import com.rafsan.newsapp.core.repository.NewsRepositoryImpl
import com.rafsan.newsapp.domain.repository.FavoriteRepository
import com.rafsan.newsapp.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    @Named("ApiKey")
    fun provideApiKey(): String {
        return BuildConfig.NEWS_API_KEY
    }

    @Provides
    @Singleton
    @Named("PageSize")
    fun providePageSize(): Int = PagingConstants.DEFAULT_PAGE_SIZE

    @Provides
    @Singleton
    fun provideNewsRepository(
        api: NewsApi,
        db: NewsDatabase,
        dao: NewsDao,
        @Named("ApiKey") apiKey: String,
        @Named("PageSize") pageSize: Int
    ): NewsRepository {
        return NewsRepositoryImpl(api, db, dao, apiKey, pageSize)
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(
        newsDao: NewsDao
    ): FavoriteRepository {
        return FavoriteRepositoryImpl(newsDao)
    }
}