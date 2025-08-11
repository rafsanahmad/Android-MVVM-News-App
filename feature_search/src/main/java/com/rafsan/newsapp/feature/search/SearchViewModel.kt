package com.rafsan.newsapp.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    private val query = MutableStateFlow("")

    val results: StateFlow<PagingData<NewsArticle>> =
        query.flatMapLatest { q ->
            if (q.isBlank()) kotlinx.coroutines.flow.flowOf(PagingData.empty())
            else repository.searchNews(q)
        }.stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    fun onQueryChanged(newQuery: String) { query.value = newQuery }
}