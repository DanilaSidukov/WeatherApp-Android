package com.sidukov.weatherapp.data.remote.api

import com.sidukov.weatherapp.domain.daily_body.ForecastBody
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("v1/forecast")
    suspend fun currentDayForecast(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("hourly") hourly: String = "temperature_2m,relativehumidity_2m,rain,showers,snowfall",
        @Query("current_weather") currentWeather: Boolean = true,
        // timezone values from here https://en.wikipedia.org/wiki/List_of_tz_database_time_zones
        @Query("timezone") timezone: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): ForecastBody

    // @GET("v1/forecast")
    suspend fun weeklyDailyForecast()

}