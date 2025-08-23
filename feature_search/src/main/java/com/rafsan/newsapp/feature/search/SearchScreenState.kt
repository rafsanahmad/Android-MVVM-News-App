package com.rafsan.newsapp.feature.search

sealed class SearchScreenState {
    object Empty : SearchScreenState()
    object Searching : SearchScreenState()
    data class QueryTooShort(val minLength: Int) : SearchScreenState()
}
