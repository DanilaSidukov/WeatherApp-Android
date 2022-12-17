package com.sidukov.weatherapp.domain.daily_body

import java.time.LocalDateTime

data class DailyForecastRequestBody(
    val latitude: Float,
    val longitude: Float,
    val timezone: String,
    val startDate: LocalDateTime,
    val endDate: String,
    val daily: String = "weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset"
)