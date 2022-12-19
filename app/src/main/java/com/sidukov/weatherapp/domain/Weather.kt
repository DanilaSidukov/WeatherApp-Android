package com.sidukov.weatherapp.domain

import com.sidukov.weatherapp.data.remote.api.WeatherAPI

//Создаю дата (как бы объект данных), который имеет поля (данные)

data class CurrentWeather(
    val date: String,
    val imageMain: Pair<Int, Int>,
    val temperature: Int,
    val humidity: Int,
    val description: Int,
    val arcAngle: Float = 0f,
    val currentWeatherCode: Int,
    val precipitation: Float,
    val dayTimeDigest: Int,
    val nightTimeDigest: Int
)

data class WeatherShort(
    val hour: String,
    val image: Int,
    val temperature: Int,
    val sunrise: String,
    val sunset: String
)

