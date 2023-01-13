package com.sidukov.weatherapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EntityLocation::class], version = 1)
abstract class DatabaseLocation : RoomDatabase() {

    abstract fun daoLocation(): LocationDao

}