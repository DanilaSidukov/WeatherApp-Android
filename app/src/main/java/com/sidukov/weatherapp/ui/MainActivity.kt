package com.sidukov.weatherapp.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.di.injectViewModel
import com.sidukov.weatherapp.ui.common.ViewPagerAdapter
import com.sidukov.weatherapp.ui.fragment_location.OnWeatherCardClicked
import java.time.LocalDateTime
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnWeatherCardClicked {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mainViewModel: MainViewModel

    private lateinit var viewPager2: ViewPager2

    @SuppressLint("ResourceAsColor", "RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsCompat.Type.systemBars()

        WeatherApplication.appComponent.inject(this)

        viewPager2 = findViewById(R.id.view_pager_2)

        mainViewModel = injectViewModel(viewModelFactory)
        var sharedCity = mainViewModel.locationRepository.settings.savedLocation
        if (sharedCity.isNullOrBlank() || sharedCity.isEmpty()) sharedCity = " "
        viewPager2.adapter = ViewPagerAdapter(this)

        if (sharedCity == " ") viewPager2.isUserInputEnabled = false

        setNightMode()

    }

    override fun onWeatherCardClicked() {
        viewPager2.isUserInputEnabled = true
        viewPager2.setCurrentItem(1, true)
    }

    fun applyDayNight(state: Int) {
        if (state == OnDayNightStateChanged.DAY) AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        supportFragmentManager.fragments.forEach {
            if (it is OnDayNightStateChanged) it.onDayNightApplied(state)
        }
    }

    fun setNightMode(){
        val nightModeFlags: Int
        if (LocalDateTime.now().hour in 22..23 || LocalDateTime.now().hour in 0..6) {
            nightModeFlags = Configuration.UI_MODE_NIGHT_YES
        } else {
            nightModeFlags = Configuration.UI_MODE_NIGHT_NO
        }

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) applyDayNight(OnDayNightStateChanged.DAY)
        else applyDayNight(OnDayNightStateChanged.NIGHT)
    }

}

interface OnDayNightStateChanged {

    fun onDayNightApplied(state: Int)

    companion object{
        const val DAY = 1
        const val NIGHT = 2
    }
}
