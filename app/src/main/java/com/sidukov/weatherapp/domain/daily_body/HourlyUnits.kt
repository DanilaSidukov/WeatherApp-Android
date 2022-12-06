package com.sidukov.weatherapp.domain.daily_body

import com.google.gson.annotations.SerializedName

data class HourlyUnits(
    @SerializedName("rain")
    val rain: String,
    @SerializedName("showers")
    val showers: String,
    @SerializedName("snowfall")
    val snowfall: String,
    @SerializedName("temperature_2m")
    val temperature: String,
    @SerializedName("relativehumidity_2m")
    val humidity: String,
    @SerializedName("timex")
    val time: String
)