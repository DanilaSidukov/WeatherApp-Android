package com.sidukov.weatherapp.domain

data class CurrentWeather(
    val date: String,
    val imageMain: Pair<Int, Int>,
    val temperature: Int,
    val humidity: Int,
    val description: Int,
    val currentWeatherCode: Int,
    val precipitation: Float,
    val dayTimeDigest: Int,
    val nightTimeDigest: Int,
    val currentAQI: Int,
)

data class WeatherShort(
    val hour: String,
    val image: Int,
    val temperature: Int,
    val sunrise: String,
    val sunset: String,
)

