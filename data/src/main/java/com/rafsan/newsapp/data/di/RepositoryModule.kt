package com.rafsan.newsapp.data.di

import com.rafsan.newsapp.core.util.NetworkMonitor
import com.rafsan.newsapp.core.util.PagingConstants
import com.rafsan.newsapp.data.database.NewsDao
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
        dao: NewsDao,
        @Named("ApiKey") apiKey: String,
        @Named("PageSize") pageSize: Int,
        networkMonitor: NetworkMonitor
    ): NewsRepository = NewsRepositoryImpl(api, dao, apiKey, pageSize, networkMonitor)

    @Provides
    @Singleton
    fun provideFavoriteRepository(
        newsDao: NewsDao
    ): FavoriteRepository = FavoriteRepositoryImpl(newsDao)
}