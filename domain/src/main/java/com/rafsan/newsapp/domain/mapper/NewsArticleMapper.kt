package com.rafsan.newsapp.domain.mapper

import com.rafsan.newsapp.core.database.entity.NewsArticleEntity
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.model.Source // Ensure this import is correct

fun NewsArticleEntity.toDomain(): NewsArticle = NewsArticle(
    id = id,
    author = author,
    content = content,
    description = description,
    publishedAt = publishedAt,
    source = Source(sourceId, sourceName ?: ""), // Assuming domain.model.Source
    title = title,
    url = url,
    urlToImage = urlToImage
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
    urlToImage = urlToImage
)