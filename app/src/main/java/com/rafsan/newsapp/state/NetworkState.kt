/*
 * *
 *  * Created by Rafsan Ahmad on 1/5/22, 11:57 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *
 */

package com.rafsan.newsapp.state

sealed class NetworkState<T>(val data: T? = null, val message: String? = null) {
    class Empty<T> : NetworkState<T>()
    class Loading<T> : NetworkState<T>()
    class Success<T>(data: T) : NetworkState<T>(data, null)

    @Suppress("UNUSED_PARAMETER")
    class Error<T>(message: String, data: T? = null) : NetworkState<T>(null, message)
}