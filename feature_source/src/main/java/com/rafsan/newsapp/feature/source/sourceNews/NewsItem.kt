/*
 * *
 *  * Created by Rafsan Ahmad on 8/23/25, 4:12PM
 *  * Copyright (c) 2025 . All rights reserved.
 *
 */

package com.rafsan.newsapp.feature.source.sourceNews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.model.Source

@Composable
fun NewsItem(
    article: NewsArticle,
    onItemClick: (NewsArticle) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(article) }
            .padding(16.dp)
    ) {
        AsyncImage(
            model = article.urlToImage,
            contentDescription = article.title
                ?: "News article image",
            modifier = Modifier.size(96.dp),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = article.title ?: "",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = article.source?.name ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = article.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsItemPreview() {
    val article = NewsArticle(
        author = "Rafsan Ahmad",
        title = "7 takeaways from the first Trump-Biden presidential debate",
        description = "The first presidential debate between Donald Trump and Joe Biden was a chaotic affair, with frequent interruptions and personal attacks.",
        url = "https://www.cnn.com/2020/09/29/politics/trump-biden-debate-takeaways/index.html",
        urlToImage = "https://cdn.cnn.com/cnnnext/dam/assets/200929224213-01-trump-biden-debate-0929-super-tease.jpg",
        publishedAt = "2020-09-30T03:18:00Z",
        content = "The first presidential debate between President Donald Trump and former Vice President Joe Biden was a chaotic and often unintelligible affair, with the two candidates frequently interrupting each other and the moderator, Fox News' Chris Wallace, struggling to maintain control.",
        source = Source(id = "cnn", name = "CNN")
    )
    NewsItem(article = article, onItemClick = {})
}
