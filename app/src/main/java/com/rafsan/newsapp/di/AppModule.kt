/*
 * *
 *  * Created by Rafsan Ahmad on 1/7/22, 12:09 AM
 *  * Copyright (c) 2022 . All rights reserved.
 *
 */

package com.rafsan.newsapp.di

import android.content.Context
import com.rafsan.newsapp.NewsApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): NewsApp {
        return app as NewsApp
    }

}