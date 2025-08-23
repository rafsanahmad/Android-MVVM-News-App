package com.rafsan.newsapp.feature.source.sources

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.rafsan.newsapp.domain.model.NewsSource

class SourcePreviewParameterProvider : PreviewParameterProvider<SourceState> {
    override val values = sequenceOf(
        SourceState.Loading,
        SourceState.Success(
            listOf(
                NewsSource(
                    id = "bbc-news",
                    name = "BBC News",
                    description = "BBC News provides trusted World and UK news as well as local and regional perspectives. Also entertainment, business, science, technology and health news.",
                    url = "https://www.bbc.co.uk/news",
                    category = "general",
                    language = "en",
                    country = "gb"
                ),
                NewsSource(
                    id = "cnn",
                    name = "CNN",
                    description = "View the latest news and breaking news today for U.S., world, weather, entertainment, politics and health at CNN.com.",
                    url = "http://us.cnn.com",
                    category = "general",
                    language = "en",
                    country = "us"
                )
            )
        ),
        SourceState.Error("An error occurred")
    )
}
