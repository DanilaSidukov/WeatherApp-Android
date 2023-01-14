package com.sidukov.weatherapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.local.db.EntityLocation
import com.sidukov.weatherapp.ui.common.ViewPagerAdapter
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import com.sidukov.weatherapp.ui.fragment_location.LocationViewModel
import com.sidukov.weatherapp.ui.fragment_weather.WeatherViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime

class MainActivity : AppCompatActivity(), OnWeatherCardListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("city", Context.MODE_PRIVATE)
        println("SHARED TO MAIN - ${sharedPreferences.getString("city", " ")}")
        view_pager_2.adapter = ViewPagerAdapter(this, this, (sharedPreferences.getString("city", " ")).toString())

        LocationFragment(sharedPreferences.getString("city", " ").toString(), this)

        val nightModeFlags: Int

        if (LocalDateTime.now().hour in 22.. 23 || LocalDateTime.now().hour in 0..6){
            println("Here night")
            nightModeFlags = Configuration.UI_MODE_NIGHT_YES
        } else {
            println("Here day")
            nightModeFlags = Configuration.UI_MODE_NIGHT_NO
        }

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) applyDayNight(OnDayNightStateChanged.DAY)
        else applyDayNight(OnDayNightStateChanged.NIGHT)

    }

    override fun onWeatherCardClicked() {
         view_pager_2.setCurrentItem(1, true)
    }

    private fun applyDayNight(state: Int){
        if (state == OnDayNightStateChanged.DAY) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        supportFragmentManager.fragments.forEach {
            if (it is OnDayNightStateChanged) it.onDayNightApplied(state)
        }
    }

}

interface OnWeatherCardListener {
    fun onWeatherCardClicked()
}

interface OnDayNightStateChanged {

    fun onDayNightApplied(state: Int)

    companion object{
        const val DAY = 1
        const val NIGHT = 2
    }
}