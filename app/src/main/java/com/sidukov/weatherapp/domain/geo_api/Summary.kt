package com.sidukov.weatherapp.domain.geo_api

data class Summary(
    val fuzzyLevel: Int,
    val numResults: Int,
    val offset: Int,
    val query: String,
    val queryTime: Int,
    val queryType: String,
    val totalResults: Int
)