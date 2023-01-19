package com.sidukov.weatherapp.domain.geo_api

data class GeocodingData(
    val results: List<Result>,
    val summary: Summary
)