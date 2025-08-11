package com.rafsan.newsapp.date

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DateFormatterTest {
    @Test
    fun formatNewsDate_with_T_formats_correctly() {
        val result = DateFormatter.formatNewsDate("2021-09-29T13:01:31Z")
        assertThat(result).isEqualTo("Sep 29, 2021 01:01 PM")
    }

    @Test
    fun formatNewsDate_without_T_returns_original() {
        val input = "2021-09-29 3:01:31 PM"
        val result = DateFormatter.formatNewsDate(input)
        assertThat(result).isEqualTo(input)
    }

    @Test
    fun formatNewsDate_empty_string_returns_empty() {
        val result = DateFormatter.formatNewsDate("")
        assertThat(result).isEmpty()
    }

    @Test
    fun formatNewsDate_invalid_format_returns_original() {
        val input = "invalid-date"
        val result = DateFormatter.formatNewsDate(input)
        assertThat(result).isEqualTo(input)
    }
}