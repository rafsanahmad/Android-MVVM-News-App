package com.rafsan.newsapp.feature.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.rafsan.newsapp.core.ui.UiState
import com.rafsan.newsapp.core.util.PagingConstants
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.core.util.NetworkMonitor
import com.rafsan.newsapp.domain.usecase.GetTopHeadlinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _selectedCountryCode = MutableStateFlow(PagingConstants.DEFAULT_COUNTRY_CODE)
    val selectedCountryCode: StateFlow<String> = _selectedCountryCode.asStateFlow()

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val headlines: StateFlow<PagingData<NewsArticle>> =
        selectedCountryCode
            .distinctUntilChanged()
            .flatMapLatest { countryCode ->
                getTopHeadlinesUseCase(countryCode = countryCode)
            }
            .onStart { uiState.value = UiState.Loading }
            .catch { e ->
                Timber.e(e, "Failed to load headlines")
                uiState.value = UiState.Error(e.message ?: "Unknown error", e)
            }
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    val uiState: MutableStateFlow<UiState<Unit>> = MutableStateFlow(UiState.Idle)

    fun selectCountry(countryCode: String) {
        _selectedCountryCode.value = countryCode
    }
}