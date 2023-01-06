package com.sidukov.weatherapp.domain

data class WeatherDescription(
    val name: Int,
    var information: String,
    val progress: Int,
    val image: Int,
)