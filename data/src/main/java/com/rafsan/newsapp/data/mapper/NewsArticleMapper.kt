package com.rafsan.newsapp.data.mapper

import com.rafsan.newsapp.data.database.entity.NewsArticleEntity
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.model.Source

fun NewsArticleEntity.toDomain(): NewsArticle = NewsArticle(
    id = id,
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = Source(sourceId, sourceName ?: ""),
    title = title,
    url = url,
    urlToImage = urlToImage,
    isFavorite = isFavorite
)

fun NewsArticle.toEntity(): NewsArticleEntity = NewsArticleEntity(
    id = id,
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    sourceId = source?.id,
    sourceName = source?.name,
    title = title,
    url = url,
    urlToImage = urlToImage,
    isFavorite = isFavorite
)


