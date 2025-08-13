package com.rafsan.newsapp.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.usecase.ManageNewsFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val manageNewsFavoriteUseCase: ManageNewsFavoriteUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val articleUrl: String? = savedStateHandle.get<String>("url")
    private var currentArticleForFavoriteAction: NewsArticle? = null

    private val _uiState = MutableStateFlow<DetailScreenState>(DetailScreenState.Loading)
    val uiState: StateFlow<DetailScreenState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        // Attempt to load initial data if enough info is in SavedStateHandle
        // However, a full NewsArticle object is preferred.
        val title: String? = savedStateHandle.get<String>("title")
        val imageUrl: String? = savedStateHandle.get<String>("image")
        val content: String? = savedStateHandle.get<String>("content") // Assuming content might be passed
        val publishedAt: String? = savedStateHandle.get<String>("publishedAt")
        val sourceName: String? = savedStateHandle.get<String>("sourceName")

        if (articleUrl != null) {
            // Reconstruct a partial NewsArticle for initial display and favorite check.
            // This is a simplified model. Ideally, the Details screen fetches its own full data if needed,
            // or a more complete object is passed.
            val articleFromNav = NewsArticle(
                id = null, // ID typically comes from DB or full fetch
                url = articleUrl,
                title = title,
                urlToImage = imageUrl,
                content = content, // Content is often fetched by Details screen itself
                publishedAt = publishedAt,
                source = sourceName?.let { com.rafsan.newsapp.domain.model.Source(null, it) },
                author = savedStateHandle.get<String>("author"),
                description = savedStateHandle.get<String>("description")
            )
            setArticle(articleFromNav) // Initialize with what we have
            _uiState.value = DetailScreenState.Success(articleFromNav) // Show what we have initially
        } else {
            _uiState.value = DetailScreenState.Error("Article details not found.")
        }
    }

    // Call this method when the screen has the definitive NewsArticle object
    // (e.g., after fetching from a repository if not all data is passed via navigation)
    fun setArticle(article: NewsArticle) {
        currentArticleForFavoriteAction = article
        article.url?.let {
            viewModelScope.launch {
                _isFavorite.value = manageNewsFavoriteUseCase.isFavorite(it)
            }
        }
        // If the article passed to setArticle is more complete, update the UI state
        if (_uiState.value is DetailScreenState.Success) {
            val currentSuccessState = _uiState.value as DetailScreenState.Success
            if (currentSuccessState.article != article) { // Update if different
                _uiState.value = DetailScreenState.Success(article)
            }
        } else if (_uiState.value !is DetailScreenState.Success) {
             _uiState.value = DetailScreenState.Success(article)
        }
    }

    fun toggleFavorite() {
        currentArticleForFavoriteAction?.let { articleToToggle ->
            // Ensure the article has a URL, as it's used as a key in DB
            if (articleToToggle.url == null) {
                Timber.w("Cannot toggle favorite for article with null URL.")
                // Optionally update UI with an error message
                _uiState.value = DetailScreenState.Error("Cannot favorite article: Missing URL.")
                return
            }

            viewModelScope.launch {
                try {
                    if (_isFavorite.value) {
                        manageNewsFavoriteUseCase.removeFavorite(articleToToggle)
                        _isFavorite.value = false
                        Timber.d("Removed favorite: %s", articleToToggle.url)
                    } else {
                        manageNewsFavoriteUseCase.addFavorite(articleToToggle)
                        _isFavorite.value = true
                        Timber.d("Saved favorite: %s", articleToToggle.url)
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to toggle favorite")
                    _uiState.value = DetailScreenState.Error("Error updating favorite status.")
                    // Revert optimistic update on error
                    // _isFavorite.value = !_isFavorite.value 
                }
            }
        } ?: run {
            Timber.w("Current article for favorite action is null.")
            _uiState.value = DetailScreenState.Error("Article data not available to update favorite.")
        }
    }
}

// Sealed class for Details Screen UI State (Task 3 & 10)
sealed class DetailScreenState {
    object Loading : DetailScreenState()
    data class Success(val article: NewsArticle) : DetailScreenState()
    data class Error(val message: String) : DetailScreenState()
}