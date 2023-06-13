package com.sidukov.weatherapp.di

import com.sidukov.weatherapp.data.local.db.LocationDao
import com.sidukov.weatherapp.data.local.settings.Settings
import com.sidukov.weatherapp.data.remote.LocationRepository
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.data.remote.api.AqiAPI
import com.sidukov.weatherapp.data.remote.api.GeoAPI
import com.sidukov.weatherapp.data.remote.api.WeatherAPI
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideWeatherRepository(
        weatherApi: WeatherAPI,
        geoAPI: GeoAPI,
        aqiAPI: AqiAPI,
        locationDao: LocationDao,
        settings: Settings,
    ): WeatherRepository = WeatherRepository(weatherApi, geoAPI, aqiAPI, locationDao, settings)

    @Singleton
    @Provides
    fun provideLocationRepository(
         locationDao: LocationDao,
         settings: Settings
    ): LocationRepository = LocationRepository(locationDao, settings)



}