package com.rafsan.newsapp.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.usecase.SaveFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val saveFavorite: SaveFavoriteUseCase
) : ViewModel() {
    fun onSaveFavorite(article: NewsArticle) {
        viewModelScope.launch {
            try {
                saveFavorite(article)
                Timber.d("Saved favorite: %s", article.url)
            } catch (e: Exception) {
                Timber.e(e, "Failed to save favorite")
            }
        }
    }
}