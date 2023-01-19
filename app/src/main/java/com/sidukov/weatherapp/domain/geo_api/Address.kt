package com.sidukov.weatherapp.domain.geo_api

import com.google.gson.annotations.SerializedName

data class Address(
    var country: String,
    val countryCode: String,
    val countryCodeISO3: String,
    @SerializedName("countrySecondarySubdivision")
    val district: String,
    val countrySubdivision: String,
    @SerializedName("countryTertiarySubdivision")
    val tertiaryDistrict: String,
    val freeformAddress: String,
    val localName: String,
    @SerializedName("municipality")
    val city: String,
    val postalCode: String,
    val streetName: String,
    @SerializedName("municipalitySubdivision")
    val town: String
)