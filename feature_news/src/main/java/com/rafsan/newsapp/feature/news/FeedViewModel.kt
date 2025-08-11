package com.rafsan.newsapp.feature.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    repository: NewsRepository
) : ViewModel() {
    val headlines: StateFlow<PagingData<NewsArticle>> =
        repository.getTopHeadlines(countryCode = "us")
            .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())
}