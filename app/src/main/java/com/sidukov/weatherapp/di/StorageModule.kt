package com.sidukov.weatherapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    ).addMigrations(MIGRATION_1_2)
        .build()

    @Singleton
    @Provides
    fun provideLocationDao(database: DatabaseLocation) : LocationDao = database.daoLocation()

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Contact ADD COLUMN seller_id TEXT NOT NULL DEFAULT ''")
        }
    }

}