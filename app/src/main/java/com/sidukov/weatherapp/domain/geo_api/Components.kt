package com.sidukov.weatherapp.domain.geo_api

import com.google.gson.annotations.SerializedName

data class Components(
    @SerializedName("ISO_3166-1_alpha-2")
    val ISO_3166_alpha_2: String,
    @SerializedName("ISO_3166-1_alpha-3")
    val ISO_3166_alpha_1: String,
    @SerializedName("ISO_3166-2")
    val ISO_3166: List<String>,
    val _category: String,
    val _type: String,
    val continent: String,
    val country: String,
    val country_code: String,
    val county: String,
    val highway: String,
    val municipality: String,
    val postcode: String,
    val region: String,
    val road: String,
    val state: String,
    val village: String
)