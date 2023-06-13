package com.sidukov.weatherapp.data.remote.api

import com.sidukov.weatherapp.domain.geo_api.GeocodingData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface GeoAPI {

    @GET("search/2/geocode/{city}.json")
    suspend fun geoData(
        @Path("city") city: String,
        @Query("view") view: String = "Unified",
        @Query("key") key: String = "sHOgdqa34WjcDtweEdBGyhe9FA4WzL1i"
    ): GeocodingData
}