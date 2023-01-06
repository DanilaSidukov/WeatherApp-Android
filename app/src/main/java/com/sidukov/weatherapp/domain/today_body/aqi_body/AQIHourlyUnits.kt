package com.sidukov.weatherapp.domain.today_body.aqi_body

import com.google.gson.annotations.SerializedName

data class AQIHourlyUnits(
    @SerializedName("european_aqi")
    val europeanAqi: String,
    @SerializedName("time")
    val time: String,
)