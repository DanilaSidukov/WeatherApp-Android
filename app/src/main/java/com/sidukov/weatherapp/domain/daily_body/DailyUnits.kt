package com.sidukov.weatherapp.domain.daily_body

import com.google.gson.annotations.SerializedName

data class DailyUnits(
    @SerializedName("sunrise")
    val sunrise: String,
    @SerializedName("sunset")
    val sunset: String,
    @SerializedName("temperature_2m_max")
    val temperature_max: String,
    @SerializedName("temperature_2m_min")
    val temperature_min: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("weathercode")
    val weathercode: String,
)