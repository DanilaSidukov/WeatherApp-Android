package com.sidukov.weatherapp.domain

import android.widget.ProgressBar

data class WeatherDescription(
    val name: Int,
    // передавать string
    var information: String,
    // переименовать на что-то осмысленное, например progress
    val progress: Int,
    val image: Int
)