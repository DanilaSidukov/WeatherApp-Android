package com.sidukov.weatherapp.domain.today_body.aqi_body

import com.google.gson.annotations.SerializedName

data class AQIBody(
    @SerializedName("generationtime_ms")
    val generationtime_ms: Float,
    @SerializedName("hourly")
    val AQIHourly: AQIHourly,
    @SerializedName("hourly_units")
    val hourly_units: AQIHourlyUnits,
    @SerializedName("latitude")
    val latitude: Float,
    @SerializedName("longitude")
    val longitude: Float,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("timezone_abbreviation")
    val timezone_abbreviation: String,
    @SerializedName("utc_offset_seconds")
    val utc_offset_seconds: Int
)