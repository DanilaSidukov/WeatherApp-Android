package com.sidukov.weatherapp.domain.today_body

import java.time.LocalDateTime

data class TodayForecastRequestBody(
    val latitude: Float,
    val longitude: Float,
    val timezone: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val hourly: String = "temperature_2m,relativehumidity_2m,rain,snowfall,cloudcover_mid",
    val currentWeather: Boolean = true,
)

