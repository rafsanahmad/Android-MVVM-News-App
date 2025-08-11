package com.rafsan.newsapp.date

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    fun formatNewsDate(strCurrentDate: String): String {
        return try {
            if (strCurrentDate.isNotEmpty() && strCurrentDate.contains("T")) {
                val local = Locale("US")
                var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", local)
                val newDate: Date? = format.parse(strCurrentDate)
                format = SimpleDateFormat("MMM dd, yyyy hh:mm a", local)
                newDate?.let { format.format(it) } ?: strCurrentDate
            } else strCurrentDate
        } catch (e: Exception) {
            strCurrentDate
        }
    }
}