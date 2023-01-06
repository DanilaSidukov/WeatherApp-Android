package com.sidukov.weatherapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("city", Context.MODE_PRIVATE)

        if (sharedPreferences.getString("city", "").toString() == "") {
            println("CITY NOW SHARED = ${sharedPreferences.getString("city", "")}")
            openFragmentLocation(LocationFragment(""), R.id.container)
        } else {
            println("CITY NOW SHARED = ${sharedPreferences.getString("city", "")}")
            openFragmentAppTopBar(WeatherFragment(sharedPreferences.getString("city", "")
                .toString()), R.id.container)
        }
    }

    private fun openFragmentAppTopBar(f: Fragment, idHolder: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, f).commit()
    }

    private fun openFragmentLocation(f: Fragment, idHolder: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, f).commit()
    }

}