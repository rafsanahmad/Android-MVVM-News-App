package com.rafsan.newsapp.data.di

import com.rafsan.newsapp.domain.repository.FavoriteRepository
import com.rafsan.newsapp.domain.repository.NewsRepository
import com.rafsan.newsapp.domain.repository.SourceRepository
import com.rafsan.newsapp.domain.usecase.GetNewsBySourceUseCase
import com.rafsan.newsapp.domain.usecase.GetSourcesUseCase
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

    @Provides
    @Singleton
    fun provideGetSourcesUseCase(repo: SourceRepository): GetSourcesUseCase =
        GetSourcesUseCase(repo)

    @Provides
    @Singleton
    fun provideGetNewsBySourceUseCase(repo: NewsRepository): GetNewsBySourceUseCase =
        GetNewsBySourceUseCase(repo)
}