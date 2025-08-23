package com.rafsan.newsapp.feature.source

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.usecase.GetNewsBySourceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourceNewsViewModel @Inject constructor(
    private val getNewsBySourceUseCase: GetNewsBySourceUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _newsState: MutableStateFlow<PagingData<NewsArticle>> =
        MutableStateFlow(PagingData.empty())
    val newsState: StateFlow<PagingData<NewsArticle>> = _newsState.asStateFlow()

    init {
        val sourceId = savedStateHandle.get<String>("sourceId")
        if (!sourceId.isNullOrEmpty()) {
            getNews(sourceId)
        }
    }

    private fun getNews(sourceId: String) {
        viewModelScope.launch {
            getNewsBySourceUseCase(sourceId)
                .cachedIn(viewModelScope)
                .collect {
                    _newsState.value = it
                }
        }
    }
}
