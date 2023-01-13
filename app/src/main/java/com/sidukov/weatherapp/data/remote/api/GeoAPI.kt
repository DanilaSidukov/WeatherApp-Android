package com.sidukov.weatherapp.data.remote.api

import com.sidukov.weatherapp.domain.geo_api.GeocodingData
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoAPI {

    @GET("geocode/v1/json")
    suspend fun geoData(
        @Query("q") city: String,
        @Query("key") key: String = "2dd6c604847a4c3fbb31b84af26cc11b",
        @Query("pretty") pretty: Int = 1,
        @Query("no_annotations") no_annotations: Int = 1,
    ): GeocodingData
}