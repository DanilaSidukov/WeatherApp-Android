package com.sidukov.weatherapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Insets
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.ui.common.ViewPagerAdapter
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment
import com.sidukov.weatherapp.ui.fragment_weather.WeatherViewModel
import kotlinx.android.synthetic.main.activity_main.*

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

    }

    override fun onWeatherCardClicked() {
         view_pager_2.setCurrentItem(1, true)
    }

}

interface OnWeatherCardListener {
    fun onWeatherCardClicked()
}