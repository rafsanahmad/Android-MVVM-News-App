package com.rafsan.newsapp.feature.details

import android.net.Uri
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
        savedStateHandle.get<String>("article")?.let { encodedArticleJson ->
            val articleJson = Uri.decode(encodedArticleJson)
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
        currentArticleForFavoriteAction?.let { article ->
            viewModelScope.launch {
                if (_isFavorite.value) {
                    manageNewsFavoriteUseCase.removeFavorite(article)
                    _isFavorite.value = false
                    _effect.send(DetailsViewEffect.ShowSnackbar(R.string.removed_from_favorites))
                } else {
                    manageNewsFavoriteUseCase.addFavorite(article)
                    _isFavorite.value = true
                    _effect.send(DetailsViewEffect.ShowSnackbar(R.string.item_added_to_favorites))
                }
            }
        }
    }
}