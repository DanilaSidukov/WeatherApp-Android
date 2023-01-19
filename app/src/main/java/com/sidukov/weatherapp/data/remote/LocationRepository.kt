package com.sidukov.weatherapp.data.remote

import com.sidukov.weatherapp.data.local.db.EntityLocation
import com.sidukov.weatherapp.data.local.db.LocationDao
import com.sidukov.weatherapp.data.local.settings.Settings
import javax.inject.Inject

class LocationRepository @Inject constructor(
    val locationDao: LocationDao,
    val settings: Settings
) {

    suspend fun getLocationData() = locationDao.getAll()

    suspend fun deleteLocationData(item: EntityLocation) = locationDao.deleteData(item)

    suspend fun deleteLocationById(name: String) = locationDao.deleteById(name)

    suspend fun getSavedLocation() = settings.savedLocation

    suspend fun setSavedLocation(newLocation: String) {
        settings.savedLocation = newLocation
    }

}
