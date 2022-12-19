package com.sidukov.weatherapp.data.remote.api

import com.sidukov.weatherapp.domain.daily_body.temp_pack.Daily
import com.sidukov.weatherapp.domain.daily_body.temp_pack.DailyForecastBody
import com.sidukov.weatherapp.domain.today_body.TodayForecastBody
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("v1/forecast")
    suspend fun currentDayForecast(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("hourly", encoded = true) hourly: String = "temperature_2m,relativehumidity_2m,precipitation,rain,showers,snowfall,weathercode",
        @Query("current_weather") currentWeather: Boolean = true,
        // timezone values from here https://en.wikipedia.org/wiki/List_of_tz_database_time_zones
        @Query("timezone") timezone: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): TodayForecastBody

    @GET("v1/forecast")
    suspend fun weeklyDailyForecast(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("daily", encoded = true) daily: String = "weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset",
        @Query("timezone") timezone: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): DailyForecastBody

}