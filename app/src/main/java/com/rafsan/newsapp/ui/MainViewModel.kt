package com.rafsan.newsapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafsan.newsapp.data.model.NewsArticle
import com.rafsan.newsapp.data.model.NewsResponse
import com.rafsan.newsapp.network.repository.MainRepository
import com.rafsan.newsapp.utils.Constants
import com.rafsan.newsapp.utils.NetworkHelper
import com.rafsan.newsapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _errorToast = MutableLiveData<String>()
    val errorToast: LiveData<String>
        get() = _errorToast

    private val _newsResponse = MutableLiveData<NetworkResult<NewsResponse>>()
    val newsResponse: LiveData<NetworkResult<NewsResponse>>
        get() = _newsResponse

    private val _searchNewsResponse = MutableLiveData<NetworkResult<NewsResponse>>()
    val searchNewsResponse: LiveData<NetworkResult<NewsResponse>>
        get() = _searchNewsResponse

    private var feedResponse: NewsResponse? = null
    var feedNewsPage = 1

    var searchNewsPage = 1
    private var searchResponse: NewsResponse? = null
    private var oldQuery: String? = null
    private var newQuery: String? = null

    init {
        fetchNews(Constants.CountryCode)
    }

    fun fetchNews(countryCode: String) {
        if (networkHelper.isNetworkConnected()) {
            _newsResponse.postValue(NetworkResult.Loading())
            try {
                viewModelScope.launch {
                    when (val response = repository.getNews(countryCode, feedNewsPage)) {
                        is NetworkResult.Success -> {
                            _newsResponse.postValue(handleFeedNewsResponse(response))
                        }
                        is NetworkResult.Error -> {
                            _newsResponse.postValue(
                                NetworkResult.Error(
                                    response.message ?: "Error"
                                )
                            )
                        }
                    }

                }
            } catch (e: Exception) {
                _newsResponse.postValue(NetworkResult.Error("Error occurred ${e.localizedMessage}"))
            }
        } else {
            _errorToast.value = "No internet available"
        }
    }

    private fun handleFeedNewsResponse(response: NetworkResult<NewsResponse>): NetworkResult<NewsResponse> {
        response.data?.let { resultResponse ->
            if (feedResponse == null) {
                feedNewsPage = 2
                feedResponse = resultResponse
            } else {
                feedNewsPage++
                val oldArticles = feedResponse?.newsArticles
                val newArticles = resultResponse.newsArticles
                oldArticles?.addAll(newArticles)
            }
            return NetworkResult.Success(feedResponse ?: resultResponse)
        }
        return NetworkResult.Error("No data found")
    }

    fun searchNews(query: String) {
        newQuery = query
        if (networkHelper.isNetworkConnected()) {
            _searchNewsResponse.postValue(NetworkResult.Loading())
            try {
                viewModelScope.launch {
                    when (val response = repository.searchNews(query, searchNewsPage)) {
                        is NetworkResult.Success -> {
                            _searchNewsResponse.postValue(handleSearchNewsResponse(response))
                        }
                        is NetworkResult.Error -> {
                            _searchNewsResponse.postValue(
                                NetworkResult.Error(
                                    response.message ?: "Error"
                                )
                            )
                        }
                    }

                }
            } catch (e: Exception) {
                _searchNewsResponse.postValue(NetworkResult.Error("Error occurred ${e.localizedMessage}"))
            }
        } else {
            _errorToast.value = "No internet available"
        }
    }

    private fun handleSearchNewsResponse(response: NetworkResult<NewsResponse>): NetworkResult<NewsResponse> {
        response.data?.let { resultResponse ->
            if (searchResponse == null || oldQuery != newQuery) {
                searchNewsPage = 2
                searchResponse = resultResponse
                oldQuery = newQuery
            } else {
                searchNewsPage++
                val oldArticles = searchResponse?.newsArticles
                val newArticles = resultResponse.newsArticles
                oldArticles?.addAll(newArticles)
            }
            return NetworkResult.Success(searchResponse ?: resultResponse)
        }
        return NetworkResult.Error("No data found")
    }

    fun hideErrorToast() {
        _errorToast.value = ""
    }

    fun saveNews(news: NewsArticle) = viewModelScope.launch {
        repository.upsert(news)
    }

    fun getFavoriteNews() = repository.getSavedNews()

    fun deleteNews(news: NewsArticle) = viewModelScope.launch {
        repository.deleteNews(news)
    }
}