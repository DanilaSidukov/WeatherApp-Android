package com.sidukov.weatherapp.domain.daily_body

import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @SerializedName("temperature")
    val temperature: Float,
    @SerializedName("time")
    val time: String,
    @SerializedName("weathercode")
    val weatherCode: Int,
    @SerializedName("winddirection")
    val windDirection: Float,
    @SerializedName("windspeed")
    val windSpeed: Float
)