package com.sidukov.weatherapp.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntityLocation(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "temperature") val temperature: Int,
    @ColumnInfo(name = "image") val image: Int,
)
