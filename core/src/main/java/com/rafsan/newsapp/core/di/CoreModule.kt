package com.rafsan.newsapp.core.di

import android.content.Context
import androidx.room.Room
import com.rafsan.newsapp.core.database.NewsDao
import com.rafsan.newsapp.core.database.NewsDatabase
import com.rafsan.newsapp.core.network.NewsApi
import com.rafsan.newsapp.core.repository.NewsRepositoryImpl
import com.rafsan.newsapp.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://newsapi.org")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsApi = retrofit.create(NewsApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NewsDatabase =
        Room.databaseBuilder(context, NewsDatabase::class.java, "news.db").build()

    @Provides
    @Singleton
    fun provideDao(db: NewsDatabase): NewsDao = db.newsDao()

    @Provides
    @Singleton
    fun provideRepository(api: NewsApi, db: NewsDatabase, dao: NewsDao): NewsRepository =
        NewsRepositoryImpl(api = api, db = db, dao = dao, apiKey = "YOUR_API_KEY", pageSize = 15)
}