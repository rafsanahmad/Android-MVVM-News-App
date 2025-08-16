package com.rafsan.newsapp.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.newsapp.domain.model.NewsArticle
import com.rafsan.newsapp.domain.model.Source
import com.rafsan.newsapp.domain.usecase.ManageNewsFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val manageNewsFavoriteUseCase: ManageNewsFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val articleUrl: String? = savedStateHandle.get<String>("url")
    private var currentArticleForFavoriteAction: NewsArticle? = null

    private val _uiState = MutableStateFlow<DetailScreenState>(DetailScreenState.Loading)
    val uiState: StateFlow<DetailScreenState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _effect = Channel<DetailsViewEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Attempt to load initial data if enough info is in SavedStateHandle
        val title: String? = savedStateHandle.get<String>("title")
        val imageUrl: String? = savedStateHandle.get<String>("image")
        val content: String? = savedStateHandle.get<String>("content")
        val publishedAt: String? = savedStateHandle.get<String>("publishedAt")
        val sourceName: String? = savedStateHandle.get<String>("sourceName")

        if (articleUrl != null) {
            val articleFromNav = NewsArticle(
                url = articleUrl,
                title = title,
                urlToImage = imageUrl,
                content = content,
                publishedAt = publishedAt,
                source = sourceName?.let { Source(null, it) },
                author = savedStateHandle.get<String>("author"),
                description = savedStateHandle.get<String>("description")
            )
            setArticle(articleFromNav)
            _uiState.value = DetailScreenState.Success(articleFromNav)
        } else {
            _uiState.value = DetailScreenState.Error("Article details not found.")
        }
    }

    fun setArticle(article: NewsArticle) {
        currentArticleForFavoriteAction = article
        article.url?.let {
            viewModelScope.launch {
                _isFavorite.value = manageNewsFavoriteUseCase.isFavorite(it)
            }
        }
        if (_uiState.value !is DetailScreenState.Success || (_uiState.value as DetailScreenState.Success).article != article) {
            _uiState.value = DetailScreenState.Success(article)
        }
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