package com.sidukov.weatherapp.domain.daily_body

data class DailyForecastBody(
    val daily: Daily,
    val daily_units: DailyUnits,
    val elevation: Float,
    val generationtime_ms: Float,
    val latitude: Float,
    val longitude: Float,
    val timezone: String,
    val timezone_abbreviation: String,
    val utc_offset_seconds: Int,
)