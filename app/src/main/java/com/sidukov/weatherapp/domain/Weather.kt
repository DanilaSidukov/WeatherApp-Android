package com.sidukov.weatherapp.domain

//Создаю дата (как бы объект данных), который имеет поля (данные)

data class Weather(
    val date: String,
    val imageMain: Pair<Int, Int>,
    val temperature: Int,
    val humidity: Int,
    val description: Int
)


