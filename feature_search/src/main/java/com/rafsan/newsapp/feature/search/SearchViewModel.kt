package com.rafsan.newsapp.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rafsan.newsapp.core.util.PagingConstants
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.usecase.SearchNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchNewsUseCase: SearchNewsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val sourceId: String? = savedStateHandle["sourceId"]

    // This StateFlow handles the active query typed by the user.
    val currentQuery: StateFlow<String> = query.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<PagingData<NewsArticle>> = // Renamed for clarity
        query
            .debounce(PagingConstants.DEBOUNCE_INTERVAL)
            .distinctUntilChanged()
            .flatMapLatest { currentSearchQuery ->
                if (currentSearchQuery.length < PagingConstants.MIN_SEARCH_LENGTH) {
                    flowOf(PagingData.empty())
                } else {
                    searchNewsUseCase(currentSearchQuery, sourceId)
                }
            }
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    fun onQueryChanged(newQuery: String) {
        Timber.d("Search query changed: %s", newQuery)
        query.value = newQuery
    }
}