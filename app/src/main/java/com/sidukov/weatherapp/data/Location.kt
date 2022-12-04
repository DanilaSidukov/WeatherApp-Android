package com.sidukov.weatherapp.data

data class Location(
    val nameLocation: String,
    val location: Boolean,
    val checkImage: Boolean,
    val gpsImage: Boolean,
    val temperatureCurrent: String,
    val date: String,
    val weatherImage: Int
)