package com.sidukov.weatherapp.data.remote.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIClient {

    private const val BASE_URL = "https://api.open-meteo.com"

    private var retrofitBase: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var weatherApiClient: WeatherAPI = retrofitBase.create(WeatherAPI::class.java)

    private const val GEO_URL = "https://api.opencagedata.com"

    private var retrofitGeo: Retrofit = Retrofit.Builder()
        .baseUrl(GEO_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var geoApiClient: GeoAPI = retrofitGeo.create(GeoAPI::class.java)


}