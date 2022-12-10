package com.sidukov.weatherapp.data.remote.api

import com.sidukov.weatherapp.domain.geo_api.GeocodingData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GeoAPI {

    @GET("geocode/v1/json")
    suspend fun geoData(
        @Query("q") city: String,
        @Query("key") key: String = "7b87359d24314f0b9b902a41e41371bf",
        @Query("pretty") pretty: Int = 1,
        @Query("no_annotations") no_annotations: Int = 1
    ): GeocodingData
}