package com.sidukov.weatherapp.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface LocationDao {

    @Query("SELECT * FROM entitylocation")
    suspend fun getAll(): List<EntityLocation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(locationData: EntityLocation)

    @Query("DELETE FROM entitylocation WHERE name = :name")
    suspend fun deleteById(name: String?)

    @Delete
    suspend fun deleteData(locationData: EntityLocation)
}