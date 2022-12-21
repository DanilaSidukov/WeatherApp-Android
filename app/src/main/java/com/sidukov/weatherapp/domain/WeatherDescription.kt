package com.sidukov.weatherapp.domain

data class WeatherDescription(
    val name: Int,
    // передавать string
    var information: String,
    // переименовать на что-то осмысленное, например progress
    val progressBar: Int,
    val image: Int
)