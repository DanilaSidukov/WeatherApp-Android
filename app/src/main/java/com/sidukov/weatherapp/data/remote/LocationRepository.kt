package com.sidukov.weatherapp.data.remote

import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.local.LocationDao
import com.sidukov.weatherapp.domain.Location

class LocationRepository(
    private val locationDao: LocationDao
) {

    suspend fun getLocationData() = locationDao.getAll()
}
