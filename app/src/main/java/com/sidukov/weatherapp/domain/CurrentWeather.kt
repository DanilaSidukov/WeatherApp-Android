package com.sidukov.weatherapp.domain

//Создаю дата (как бы объект данных), который имеет поля (данные)

data class CurrentWeather(
    val date: String,
    val imageMain: Pair<Int, Int>,
    val temperature: Int,
    val humidity: Int,
    val description: Int
)

data class HourlyWeather(
    val hour: String,
    val image: Pair <Int, Int>,
    val temperature: Int
)
