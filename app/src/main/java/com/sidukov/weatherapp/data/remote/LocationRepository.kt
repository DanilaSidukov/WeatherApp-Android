package com.sidukov.weatherapp.data.remote

import com.sidukov.weatherapp.data.local.EntityLocation
import com.sidukov.weatherapp.data.local.LocationDao

class LocationRepository(
    private val locationDao: LocationDao,
    private val item: EntityLocation,
) {

    suspend fun getLocationData() = locationDao.getAll()

    suspend fun deleteLocationData() = locationDao.deleteData(item)

    suspend fun deleteLocationById() = locationDao.deleteById(item.name)

}
