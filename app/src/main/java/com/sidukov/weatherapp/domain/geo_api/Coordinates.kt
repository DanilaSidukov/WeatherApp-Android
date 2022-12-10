package com.sidukov.weatherapp.domain.geo_api

import com.google.gson.annotations.SerializedName

data class Coordinates(
    @SerializedName("lat")
    val latitude: Float,
    @SerializedName("lng")
    val longitude: Float
)