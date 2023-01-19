package com.sidukov.weatherapp.di

import com.sidukov.weatherapp.ui.MainActivity
import com.sidukov.weatherapp.ui.fragment_location.LocationFragment
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        ApiModule::class,
        AppModule::class,
        RepositoryModule::class,
        StorageModule::class,
        ViewModelModule::class
    ]
)
interface WeatherAppComponent {

    fun inject(activity: MainActivity)
    fun inject(fragment: LocationFragment)
    fun inject(fragment: WeatherFragment)
}