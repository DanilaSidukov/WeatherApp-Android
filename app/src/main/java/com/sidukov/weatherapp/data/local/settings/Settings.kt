package com.sidukov.weatherapp.data.local.settings

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.edit
import com.sidukov.weatherapp.ui.common.message

class Settings(private val context: Context) {

    companion object {
        const val SETTINGS_NAME = "settings"
        const val FIELD_CITY = "city"
    }

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)

    var savedLocation: String?
        get() = sharedPreferences.getString(FIELD_CITY, null)
        set(value) = sharedPreferences.edit {
            putString(FIELD_CITY, value?: " ")
            this.apply()
        }
    fun deleteValue(){
        sharedPreferences.edit().clear().apply()
    }

    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        capabilities?.let {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true
        }
        return false
    }

    fun makeToastErrorConnection() = context.message("Connection error! Please, check your internet connection")

}