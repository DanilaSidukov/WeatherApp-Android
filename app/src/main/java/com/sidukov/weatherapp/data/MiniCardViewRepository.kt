package com.sidukov.weatherapp.data

import com.sidukov.weatherapp.R

class MiniCardViewRepository {

    fun getCardView(): List<WeatherDescription>{
        return listOf(
            WeatherDescription(R.string.air_quality, "Good", R.id.progress_bar, R.drawable.ic_air_quality),
            WeatherDescription(R.string.aqi, "72", R.id.progress_bar, R.drawable.ic_air_quality),
            WeatherDescription(R.string.humidity, "43", R.id.progress_bar, R.drawable.ic_humidity),
            WeatherDescription(R.string.precipitation, "3", R.id.progress_bar, R.drawable.ic_sky_rainy)
        )

    }
}