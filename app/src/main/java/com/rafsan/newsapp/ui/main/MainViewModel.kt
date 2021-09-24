package com.rafsan.newsapp.ui.main

import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "MainViewModel"
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
    var searchResponse: NewsResponse? = null
    private var oldQuery: String = ""
    var newQuery: String = ""

    private val _isSearchActivated = MutableLiveData<Boolean>()
    val isSearchActivated: LiveData<Boolean>
        get() = _isSearchActivated

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
                val oldArticles = feedResponse?.articles
                val newArticles = resultResponse.articles
                oldArticles?.addAll(newArticles)
            }
            //Conversion
            feedResponse?.let {
                feedResponse = convertPublishedDate(it)
            }
            return NetworkResult.Success(feedResponse ?: resultResponse)
        }
        return NetworkResult.Error("No data found")
    }

    fun searchNews(query: String) {
        newQuery = query
        if (!newQuery.isEmpty()) {
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
    }

    private fun handleSearchNewsResponse(response: NetworkResult<NewsResponse>): NetworkResult<NewsResponse> {
        response.data?.let { resultResponse ->
            if (searchResponse == null || oldQuery != newQuery) {
                searchNewsPage = 2
                searchResponse = resultResponse
                oldQuery = newQuery
            } else {
                searchNewsPage++
                val oldArticles = searchResponse?.articles
                val newArticles = resultResponse.articles
                oldArticles?.addAll(newArticles)
            }
            searchResponse?.let {
                searchResponse = convertPublishedDate(it)
            }
            return NetworkResult.Success(searchResponse ?: resultResponse)
        }
        return NetworkResult.Error("No data found")
    }

    fun convertPublishedDate(currentResponse: NewsResponse): NewsResponse {
        currentResponse.let { response ->
            for (i in 0 until response.articles.size) {
                val publishedAt = response.articles[i].publishedAt
                publishedAt?.let {
                    val converted = formatDate(it)
                    response.articles[i].publishedAt = converted
                }
            }
        }
        return currentResponse
    }

    fun formatDate(strCurrentDate: String): String {
        var convertedDate = ""
        try {
            if (strCurrentDate.isNotEmpty() && strCurrentDate.contains("T")) {
                var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                val newDate: Date? = format.parse(strCurrentDate)

                format = SimpleDateFormat("MMM dd, yyyy hh:mm a")
                newDate?.let {
                    convertedDate = format.format(it)
                }
            }
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
            }
            convertedDate = strCurrentDate
        }
        return convertedDate
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

    fun clearSearch() {
        _isSearchActivated.postValue(false)
        searchResponse = null
        feedNewsPage = 1
        searchNewsPage = 1
    }

    fun enableSearch() {
        _isSearchActivated.postValue(true)
    }
}