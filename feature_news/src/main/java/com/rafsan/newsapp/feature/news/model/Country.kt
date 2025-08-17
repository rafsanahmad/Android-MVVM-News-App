package com.rafsan.newsapp.feature.news.model

fun countryCodeToEmojiFlag(countryCode: String): String {
    return countryCode
        .uppercase()
        .map { char ->
            String(Character.toChars(char.code + 0x1F1A5))
        }
        .joinToString("")
}

data class Country(
    val code: String,
    val name: String
) {
    val flag: String
        get() = countryCodeToEmojiFlag(code)
}

val supportedCountries = listOf(
    Country("ar", "Argentina"),
    Country("au", "Australia"),
    Country("at", "Austria"),
    Country("be", "Belgium"),
    Country("br", "Brazil"),
    Country("bg", "Bulgaria"),
    Country("ca", "Canada"),
    Country("cn", "China"),
    Country("co", "Colombia"),
    Country("cu", "Cuba"),
    Country("cz", "Czech Republic"),
    Country("eg", "Egypt"),
    Country("fr", "France"),
    Country("de", "Germany"),
    Country("gr", "Greece"),
    Country("hk", "Hong Kong"),
    Country("hu", "Hungary"),
    Country("in", "India"),
    Country("id", "Indonesia"),
    Country("ie", "Ireland"),
    Country("il", "Israel"),
    Country("it", "Italy"),
    Country("jp", "Japan"),
    Country("lv", "Latvia"),
    Country("lt", "Lithuania"),
    Country("my", "Malaysia"),
    Country("mx", "Mexico"),
    Country("ma", "Morocco"),
    Country("nl", "Netherlands"),
    Country("nz", "New Zealand"),
    Country("ng", "Nigeria"),
    Country("no", "Norway"),
    Country("ph", "Philippines"),
    Country("pl", "Poland"),
    Country("pt", "Portugal"),
    Country("ro", "Romania"),
    Country("ru", "Russia"),
    Country("sa", "Saudi Arabia"),
    Country("rs", "Serbia"),
    Country("sg", "Singapore"),
    Country("sk", "Slovakia"),
    Country("si", "Slovenia"),
    Country("za", "South Africa"),
    Country("kr", "South Korea"),
    Country("se", "Sweden"),
    Country("ch", "Switzerland"),
    Country("tw", "Taiwan"),
    Country("th", "Thailand"),
    Country("tr", "Turkey"),
    Country("ua", "Ukraine"),
    Country("ae", "United Arab Emirates"),
    Country("gb", "United Kingdom"),
    Country("us", "United States"),
    Country("ve", "Venezuela")
)
