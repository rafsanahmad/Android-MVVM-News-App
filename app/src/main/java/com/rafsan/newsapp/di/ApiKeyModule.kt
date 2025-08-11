package com.rafsan.newsapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import com.rafsan.newsapp.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object ApiKeyModule {

    @Provides
    @Singleton
    @Named("newsApiKey")
    fun provideNewsApiKey(): String {
        return BuildConfig.NEWS_API_KEY
    }
}