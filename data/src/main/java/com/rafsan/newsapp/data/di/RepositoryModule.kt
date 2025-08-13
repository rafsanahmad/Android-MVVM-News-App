package com.rafsan.newsapp.data.di

import com.rafsan.newsapp.core.PagingConstants
import com.rafsan.newsapp.data.database.NewsDao
import com.rafsan.newsapp.data.database.NewsDatabase
import com.rafsan.newsapp.data.network.NewsApi
import com.rafsan.newsapp.data.repository.FavoriteRepositoryImpl
import com.rafsan.newsapp.data.repository.NewsRepositoryImpl
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
    fun provideApiKey(): String = com.rafsan.newsapp.data.BuildConfig.NEWS_API_KEY

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
    ): NewsRepository = NewsRepositoryImpl(api, db, dao, apiKey, pageSize)

    @Provides
    @Singleton
    fun provideFavoriteRepository(
        newsDao: NewsDao
    ): FavoriteRepository = FavoriteRepositoryImpl(newsDao)
}