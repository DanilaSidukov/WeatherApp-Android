package com.sidukov.weatherapp.domain

//Создаю дата (как бы объект данных), который имеет поля (данные)

data class Weather(
    val date: String,
    val image: Int,
    val temperature: Int,
    val humidity: Float
)


