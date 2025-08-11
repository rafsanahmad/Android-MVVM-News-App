package com.rafsan.newsapp.core.di

import com.rafsan.newsapp.domain.repository.NewsRepository
import com.rafsan.newsapp.domain.usecase.DeleteFavoriteUseCase
import com.rafsan.newsapp.domain.usecase.GetFavoritesUseCase
import com.rafsan.newsapp.domain.usecase.GetTopHeadlinesUseCase
import com.rafsan.newsapp.domain.usecase.SaveFavoriteUseCase
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
    fun provideGetTopHeadlinesUseCase(repo: NewsRepository) = GetTopHeadlinesUseCase(repo)

    @Provides
    @Singleton
    fun provideSearchNewsUseCase(repo: NewsRepository) = SearchNewsUseCase(repo)

    @Provides
    @Singleton
    fun provideGetFavoritesUseCase(repo: NewsRepository) = GetFavoritesUseCase(repo)

    @Provides
    @Singleton
    fun provideSaveFavoriteUseCase(repo: NewsRepository) = SaveFavoriteUseCase(repo)

    @Provides
    @Singleton
    fun provideDeleteFavoriteUseCase(repo: NewsRepository) = DeleteFavoriteUseCase(repo)
}