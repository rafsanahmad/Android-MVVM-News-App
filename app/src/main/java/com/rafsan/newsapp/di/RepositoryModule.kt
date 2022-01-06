/*
 * *
 *  * Created by Rafsan Ahmad on 1/7/22, 12:12 AM
 *  * Copyright (c) 2022 . All rights reserved.
 *
 */

package com.rafsan.newsapp.di

import android.content.Context
import com.rafsan.newsapp.data.local.NewsDao
import com.rafsan.newsapp.data.local.NewsDatabase
import com.rafsan.newsapp.network.api.ApiHelper
import com.rafsan.newsapp.network.repository.INewsRepository
import com.rafsan.newsapp.network.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        NewsDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideNewsDao(db: NewsDatabase) = db.getNewsDao()

    @Singleton
    @Provides
    fun provideRepository(
        remoteDataSource: ApiHelper,
        localDataSource: NewsDao
    ) = NewsRepository(remoteDataSource, localDataSource)

    @Singleton
    @Provides
    fun provideINewsRepository(repository: NewsRepository): INewsRepository = repository
}