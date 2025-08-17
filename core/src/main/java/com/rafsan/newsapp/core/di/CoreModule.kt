package com.rafsan.newsapp.core.di

import com.rafsan.newsapp.core.util.NetworkMonitor
import com.rafsan.newsapp.core.util.NetworkMonitorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        networkMonitor: NetworkMonitorImpl
    ): NetworkMonitor
}