package com.rafsan.newsapp.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.rafsan.newsapp.core.ui.UiState
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.usecase.SearchNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchNews: SearchNewsUseCase
) : ViewModel() {
    private val query = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val results: StateFlow<PagingData<NewsArticle>> =
        query
            .debounce(500)
            .distinctUntilChanged()
            .flatMapLatest { q ->
                if (q.isBlank()) kotlinx.coroutines.flow.flowOf(PagingData.empty())
                else searchNews(q)
            }
            .onStart { uiState.value = UiState.Loading }
            .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    val uiState: MutableStateFlow<UiState<Unit>> = MutableStateFlow(UiState.Idle)

    fun onQueryChanged(newQuery: String) {
        Timber.d("Query changed: %s", newQuery)
        query.value = newQuery
    }
}