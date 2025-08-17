package com.rafsan.newsapp.core.util

import android.content.Context
import com.rafsan.newsapp.core.R
import retrofit2.HttpException
import java.io.IOException

fun getErrorMessage(error: Throwable, context: Context): String {
    return when (error) {
        is IOException -> context.getString(R.string.error_network)
        is HttpException -> {
            val code = error.code()
            if (code in 500..599) {
                context.getString(R.string.error_server)
            } else {
                context.getString(R.string.error_http, code)
            }
        }
        else -> context.getString(R.string.error_unknown)
    }
}
