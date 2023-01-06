package com.sidukov.weatherapp.domain.daily_body.temp_pack

import com.google.gson.annotations.SerializedName

data class Daily(
    @SerializedName("sunrise")
    val sunrise: List<String>,
    @SerializedName("sunset")
    val sunset: List<String>,
    @SerializedName("temperature_2m_max")
    val temperature_max: List<Float>,
    @SerializedName("temperature_2m_min")
    val temperature_min: List<Float>,
    @SerializedName("time")
    val dayOfWeek: List<String>,
    @SerializedName("weathercode")
    val weathercode: List<Int>,
)