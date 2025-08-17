package com.rafsan.newsapp.data.di

import com.rafsan.newsapp.domain.repository.FavoriteRepository
import com.rafsan.newsapp.domain.repository.NewsRepository
import com.rafsan.newsapp.domain.usecase.GetTopHeadlinesUseCase
import com.rafsan.newsapp.domain.usecase.ManageNewsFavoriteUseCase
import com.rafsan.newsapp.domain.usecase.SearchNewsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetTopHeadlinesUseCase(repo: NewsRepository): GetTopHeadlinesUseCase =
        GetTopHeadlinesUseCase(repo)

    @Provides
    @Singleton
    fun provideSearchNewsUseCase(repo: NewsRepository): SearchNewsUseCase =
        SearchNewsUseCase(repo)

    @Provides
    @Singleton
    fun provideManageNewsFavoriteUseCase(favRepo: FavoriteRepository): ManageNewsFavoriteUseCase =
        ManageNewsFavoriteUseCase(favRepo)
}