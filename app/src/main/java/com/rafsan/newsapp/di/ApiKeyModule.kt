package com.rafsan.newsapp.di

import com.rafsan.newsapp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

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