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
import com.sidukov.weatherapp.data.NTuple5
import com.sidukov.weatherapp.data.local.EntityLocation
import com.sidukov.weatherapp.data.remote.LocationRepository
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.data.remote.api.APIClient
import com.sidukov.weatherapp.data.remote.api.WeatherAPI
import com.sidukov.weatherapp.domain.CurrentWeather
import com.sidukov.weatherapp.domain.WeatherDescription
import com.sidukov.weatherapp.domain.WeatherShort
import com.sidukov.weatherapp.ui.common.ViewPagerAdapter
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import com.sidukov.weatherapp.ui.fragment_location.LocationViewModel
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment
import com.sidukov.weatherapp.ui.fragment_weather.WeatherViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect

class MainActivity : AppCompatActivity(), OnWeatherCardListener {

    private lateinit var locationViewModel: LocationViewModel
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var entityLocationList: List<EntityLocation>
    private lateinit var locationFragmentEntity: EntityLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        entityLocationList = emptyList()

        locationViewModel = LocationViewModel(
            LocationRepository(
                WeatherApplication.database.daoLocation(),
                EntityLocation(
                    "",
                    "",
                    0,
                    0,
                )
            )
        )
        locationViewModel.getLocationDataBase()
        lifecycleScope.launchWhenStarted {
            locationViewModel.locationList.collect{
                if (entityLocationList.isEmpty()) return@collect
                entityLocationList = it
            }
        }

        var locationFragmentEntityList : List<EntityLocation> = emptyList()
        entityLocationList.forEach { list ->
            weatherViewModel = WeatherViewModel(
                WeatherRepository(
                    APIClient.weatherApiClient,
                    APIClient.geoApiClient,
                    APIClient.aqiApiClient,
                    WeatherApplication.database.daoLocation(),
                    this
                ),list.name
            )
            lifecycleScope.launchWhenStarted {
                weatherViewModel.listToLocationFragment.collect{
                    locationFragmentEntity = it
                    locationFragmentEntityList = locationFragmentEntityList.plus(locationFragmentEntity)
                }
            }
        }

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("city", Context.MODE_PRIVATE)
        println("SHARED TO MAIN - ${sharedPreferences.getString("city", " ")}")

        view_pager_2.adapter = ViewPagerAdapter(this, this, (sharedPreferences.getString("city", " ")).toString(), locationFragmentEntityList)

        LocationFragment(sharedPreferences.getString("city", " ").toString(), this, locationFragmentEntityList)

    }

    override fun onWeatherCardClicked() {
         view_pager_2.setCurrentItem(1, true)
    }

}

interface OnWeatherCardListener {
    fun onWeatherCardClicked()
}