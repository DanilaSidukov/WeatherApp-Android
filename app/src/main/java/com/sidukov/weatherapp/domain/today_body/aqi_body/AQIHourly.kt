package com.sidukov.weatherapp.domain.today_body.aqi_body

import com.google.gson.annotations.SerializedName

data class AQIHourly(
    @SerializedName("european_aqi")
    val aqiList: List<Int>,
    @SerializedName("time")
    val time: List<String>,
)