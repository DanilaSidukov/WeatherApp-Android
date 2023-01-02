package com.sidukov.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface LocationDao {

    @Query("SELECT * FROM entitylocation")
    suspend fun getAll(): List<EntityLocation>

    @Insert
    suspend fun insertData(locationData: EntityLocation)

    @Query("DELETE FROM entitylocation")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteData(locationData: EntityLocation)
}