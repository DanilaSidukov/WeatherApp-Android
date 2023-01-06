package com.sidukov.weatherapp.domain.today_body

import com.google.gson.annotations.SerializedName

data class Hourly(
    @SerializedName("rain")
    val rain: List<Float>,
    @SerializedName("snowfall")
    val snowfall: List<Float>,
    @SerializedName("cloudcover_mid")
    val cloudCover: List<Float>,
    @SerializedName("temperature_2m")
    val temperature: List<Float>,
    @SerializedName("relativehumidity_2m")
    val humidity: List<Float>,
    @SerializedName("time")
    val time: List<String>,
    @SerializedName("weathercode")
    val hourlyWeatherCode: List<Int>,
    @SerializedName("precipitation")
    val precipitation: List<Float>,
)