package com.rafsan.newsapp.utils

sealed class NetworkResult<T>(val data: T? = null, val message: String? = null) {
    class Loading<T> : NetworkResult<T>()
    class Success<T>(data: T) : NetworkResult<T>(data, null)

    @Suppress("UNUSED_PARAMETER")
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(null, message)
}