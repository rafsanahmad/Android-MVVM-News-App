/*
 * *
 *  * Created by Rafsan Ahmad on 8/23/25, 4:12PM
 *  * Copyright (c) 2025 . All rights reserved.
 *
 */

package com.rafsan.newsapp.feature.source.sourceNews

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourceNewsViewModel @Inject constructor(
    private val getNewsBySourceUseCase: GetNewsBySourceUseCase,
    savedStateHandle: SavedStateHandle
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
