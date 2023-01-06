package com.sidukov.weatherapp.ui

import android.app.Application
import androidx.room.Room
import com.sidukov.weatherapp.data.local.DatabaseLocation

class WeatherApplication : Application() {

    companion object {
        lateinit var database: DatabaseLocation
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            DatabaseLocation::class.java,
            "location-list"
        ).build()
    }

}