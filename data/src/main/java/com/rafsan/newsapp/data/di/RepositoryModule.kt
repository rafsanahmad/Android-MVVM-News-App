package com.rafsan.newsapp.data.di // Updated package

import com.rafsan.newsapp.core.BuildConfig // From core
import com.rafsan.newsapp.core.PagingConstants // From core
import com.rafsan.newsapp.data.database.NewsDao // From data.database
import com.rafsan.newsapp.data.database.NewsDatabase // From data.database
import com.rafsan.newsapp.data.network.NewsApi // From data.network
import com.rafsan.newsapp.data.repository.FavoriteRepositoryImpl // From data.repository
import com.rafsan.newsapp.data.repository.NewsRepositoryImpl // From data.repository
import com.rafsan.newsapp.domain.repository.FavoriteRepository // From domain
import com.rafsan.newsapp.domain.repository.NewsRepository // From domain
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