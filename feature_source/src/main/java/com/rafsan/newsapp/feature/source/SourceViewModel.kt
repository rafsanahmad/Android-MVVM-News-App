package com.rafsan.newsapp.feature.source

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.newsapp.domain.model.NewsSource
import com.rafsan.newsapp.domain.usecase.GetSourcesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourceViewModel @Inject constructor(
    private val getSourcesUseCase: GetSourcesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SourceState>(SourceState.Loading)
    val state: StateFlow<SourceState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var originalSources: List<NewsSource> = emptyList()

    init {
        getSources()
    }

    fun getSources() {
        viewModelScope.launch {
            getSourcesUseCase()
                .onStart { _state.value = SourceState.Loading }
                .catch { e -> _state.value = SourceState.Error(e.message ?: "An error occurred") }
                .collect { sources ->
                    originalSources = sources
                    _state.value = SourceState.Success(sources)
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        val filteredSources = if (query.isBlank()) {
            originalSources
        } else {
            originalSources.filter { it.name.contains(query, ignoreCase = true) }
        }
        _state.value = SourceState.Success(filteredSources)
    }
}

sealed class SourceState {
    data object Loading : SourceState()
    data class Success(val sources: List<NewsSource>) : SourceState()
    data class Error(val message: String) : SourceState()
}
