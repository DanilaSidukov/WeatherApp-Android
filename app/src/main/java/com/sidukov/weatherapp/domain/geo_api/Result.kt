package com.sidukov.weatherapp.domain.geo_api

data class Result(
    val components: Components,
    val geometry: Coordinates,
)