package com.sidukov.weatherapp.data

//Создаю дата (как бы объект данных), который имеет поля (данные)

data class Weather(
    val day: String,
    val image: Int,
    val temperature: Int
)


