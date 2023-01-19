package com.sidukov.weatherapp.domain.geo_api

import com.google.gson.annotations.SerializedName

data class Position(
    @SerializedName("lat")
    val latitude: Float,
    @SerializedName("lon")
    val longitude: Float
)