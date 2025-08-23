package com.rafsan.newsapp.core.util

fun countryCodeToEmojiFlag(countryCode: String): String {
    // 1. It first checks if the country code is valid.
    if (countryCode.length != 2 || !countryCode.all { it.isLetter() }) {
        return "🏳️" // Return a default white flag for invalid codes.
    }

    // 2. It converts the country code to uppercase.
    // 3. It then maps each character of the country code to its corresponding regional indicator symbol.
    // 4. Finally, it joins the regional indicator symbols to form the flag emoji.
    return countryCode
        .uppercase()
        .map { char ->
            Character.toChars(char.code + (0x1F1E6 - 'A'.code))
        }
        .joinToString(separator = "") { String(it) }
}
