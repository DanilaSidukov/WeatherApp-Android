package com.sidukov.weatherapp.domain

//Создаю дата (как бы объект данных), который имеет поля (данные)

data class CurrentWeather(
    val date: String,
    val imageMain: Pair<Int, Int>,
    val temperature: Int,
    val humidity: Int,
    val description: Int,
    val arcAngle: Float = 0f
)

data class WeatherShort(
    val hour: String,
    val image: Int,
    val temperature: Int,
    val sunrise: String,
    val sunset: String
)
