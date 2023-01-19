package com.sidukov.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.sidukov.weatherapp.data.local.db.DatabaseLocation
import com.sidukov.weatherapp.data.local.db.LocationDao
import com.sidukov.weatherapp.data.local.settings.Settings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): Settings = Settings(context)

    @Singleton
    @Provides
    fun provideDatabase(context: Context): DatabaseLocation = Room.databaseBuilder(
        context,
        DatabaseLocation::class.java,
        "location-list"
    ).build()

    @Singleton
    @Provides
    fun provideLocationDao(database: DatabaseLocation) : LocationDao = database.daoLocation()

}