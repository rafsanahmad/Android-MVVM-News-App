package com.rafsan.newsapp.domain.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@Parcelize
data class Source(
    val id: String?,
    val name: String
) : Parcelable