package com.sidukov.weatherapp.data.local.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class Settings(context: Context) {

    companion object {
        const val SETTINGS_NAME = "settings"
        const val FIELD_CITY = "city"
    }

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)

    var savedLocation: String
        get() = sharedPreferences.getString(FIELD_CITY, " ") ?: " "
        set(value) = sharedPreferences.edit {
            putString(FIELD_CITY, value)
        }

}