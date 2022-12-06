package com.sidukov.weatherapp.domain.daily_body

import com.google.gson.annotations.SerializedName

data class ForecastBody(
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather,
    @SerializedName("elevation")
    val elevation: Float,
    @SerializedName("generationtime_ms")
    val generationtimeMs: Float,
    @SerializedName("hourly")
    val hourly: Hourly,
    @SerializedName("hourly_units")
    val hourlyUnits: HourlyUnits,
    @SerializedName("latitude")
    val latitude: Float,
    @SerializedName("longitude")
    val longitude: Float,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    @SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int
)