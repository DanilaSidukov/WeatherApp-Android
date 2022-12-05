package com.sidukov.weatherapp.domain

data class Location(
    val name: String,
    val shouldShowLocation: Boolean,
    val shouldShowCheckImage: Boolean,
    val shouldShowGpsImage: Boolean,
    val currentTemperature: String,
    val date: String,
    val weatherImage: Int
)