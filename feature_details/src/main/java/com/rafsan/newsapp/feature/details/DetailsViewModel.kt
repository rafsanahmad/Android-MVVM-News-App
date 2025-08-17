package com.rafsan.newsapp.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.usecase.ManageNewsFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val manageNewsFavoriteUseCase: ManageNewsFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var currentArticleForFavoriteAction: NewsArticle? = null

    private val _uiState = MutableStateFlow<DetailScreenState>(DetailScreenState.Loading)
    val uiState: StateFlow<DetailScreenState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _effect = Channel<DetailsViewEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        savedStateHandle.get<String>("article")?.let { articleJson ->
            val article = Json.decodeFromString<NewsArticle>(articleJson)
            setArticle(article)
        } ?: run {
            _uiState.value = DetailScreenState.Error("Article details not found.")
        }
    }

    private fun setArticle(article: NewsArticle) {
        currentArticleForFavoriteAction = article
        article.url?.let {
            viewModelScope.launch {
                _isFavorite.value = manageNewsFavoriteUseCase.isFavorite(it)
            }
        }
        _uiState.value = DetailScreenState.Success(article)
    }

    fun onFavoriteClicked() {
        currentArticleForFavoriteAction?.let { articleToAdd ->
            if (articleToAdd.url == null) {
                Timber.w("Cannot favorite article with null URL.")
                return
            }

            viewModelScope.launch {
                if (_isFavorite.value) {
                    _effect.send(DetailsViewEffect.ShowSnackbar(R.string.item_already_in_favorites))
                } else {
                    try {
                        manageNewsFavoriteUseCase.addFavorite(articleToAdd)
                        _isFavorite.value = true
                        _effect.send(DetailsViewEffect.ShowSnackbar(R.string.item_added_to_favorites))
                        Timber.d("Saved favorite: %s", articleToAdd.url)
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to add favorite")
                        // It would be better to send an effect for error as well
                        // For now, mapping to an error state as before
                        _uiState.value = DetailScreenState.Error("Error adding favorite.")
                    }
                }
            }
        } ?: run {
            Timber.w("Current article is null, cannot perform favorite action.")
        }
    }
}