package com.sidukov.weatherapp.ui

import android.app.Application
import com.sidukov.weatherapp.di.ApiModule
import com.sidukov.weatherapp.di.AppModule
import com.sidukov.weatherapp.di.DaggerWeatherAppComponent
import com.sidukov.weatherapp.di.RepositoryModule
import com.sidukov.weatherapp.di.StorageModule
import com.sidukov.weatherapp.di.WeatherAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class WeatherApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    var instance: WeatherApplication? = null

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    companion object {
        lateinit var appComponent: WeatherAppComponent
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = DaggerWeatherAppComponent.builder()
            .appModule(AppModule(this))
            .repositoryModule(RepositoryModule())
            .apiModule(ApiModule())
            .storageModule(StorageModule())
            .build()
    }
}