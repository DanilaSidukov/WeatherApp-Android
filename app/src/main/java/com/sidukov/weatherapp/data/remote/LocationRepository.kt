package com.sidukov.weatherapp.data.remote

import com.sidukov.weatherapp.data.local.db.LocationDao
import com.sidukov.weatherapp.data.local.settings.Settings
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationDao: LocationDao,
    val settings: Settings
) {

    suspend fun getLocationData() = locationDao.getAll()

    suspend fun deleteLocationById(name: String) = locationDao.deleteById(name)

    fun getNetworkStatus() = settings.isNetworkConnected()

    fun errorConnectionMessage() = settings.makeToastErrorConnection()

}
