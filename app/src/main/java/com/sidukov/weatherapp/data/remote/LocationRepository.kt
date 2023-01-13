package com.sidukov.weatherapp.data.remote

import com.sidukov.weatherapp.data.local.db.EntityLocation
import com.sidukov.weatherapp.data.local.db.LocationDao
import com.sidukov.weatherapp.data.local.settings.Settings

class LocationRepository(
    private val locationDao: LocationDao,
    private val item: EntityLocation,
    private val settings: Settings
) {

    suspend fun getLocationData() = locationDao.getAll()

    suspend fun deleteLocationData() = locationDao.deleteData(item)

    suspend fun deleteLocationById() = locationDao.deleteById(item.name)

    suspend fun getSavedLocation() = settings.savedLocation

    suspend fun setSavedLocation(newLocation: String) {
        settings.savedLocation = newLocation
    }

}
