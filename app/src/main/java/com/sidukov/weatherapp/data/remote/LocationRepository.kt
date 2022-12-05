package com.sidukov.weatherapp.data.remote

import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.domain.Location

class LocationRepository {

    fun getLocationData(): List<Location>{
        return listOf(
            Location("New york", true , true, true, "14", "3-12", R.drawable.ic_sky_rainy),
            Location("Dima Sidukov", false , false, false, "14", "3-12", R.drawable.ic_sun)
        )
    }
}
