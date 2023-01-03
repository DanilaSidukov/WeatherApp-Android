package com.sidukov.weatherapp.data.local

import android.widget.ImageView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntityLocation(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "temperature") val temperature: Int,
    @ColumnInfo(name = "image") val image: Int,
    @ColumnInfo(name = "checkBoolean") var checkBoolean: Boolean

)

