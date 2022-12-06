package com.sidukov.weatherapp.domain.daily_body

import java.time.LocalDateTime

data class DailyForecastRequestBody(
    val latitude: Float,
    val longitude: Float,
    val timezone: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val hourly: String = "temperature_2m,relativehumidity_2m,rain,showers,snowfall",
    val currentWeather: Boolean = true,
)