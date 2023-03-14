package com.sidukov.weatherapp.ui

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.di.*
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import java.time.LocalDateTime
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