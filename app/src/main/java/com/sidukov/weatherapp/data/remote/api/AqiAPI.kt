package com.sidukov.weatherapp.data.remote.api

import com.sidukov.weatherapp.domain.today_body.aqi_body.AQIBody
import retrofit2.http.GET
import retrofit2.http.Query

interface AqiAPI {

    @GET("v1/air-quality")
    suspend fun getCurrentAQI(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("hourly", encoded = true) hourly: String = "european_aqi",
        @Query("timezone") timezone: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): AQIBody
}