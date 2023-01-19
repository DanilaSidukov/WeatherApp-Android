package com.sidukov.weatherapp.di

import com.sidukov.weatherapp.data.remote.api.AqiAPI
import com.sidukov.weatherapp.data.remote.api.GeoAPI
import com.sidukov.weatherapp.data.remote.api.WeatherAPI
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApiModule {


    @Singleton
    @Provides
    fun provideWeatherApi(): WeatherAPI = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherAPI::class.java)

    @Singleton
    @Provides
    fun provideGeoApi(): GeoAPI = Retrofit.Builder()
        .baseUrl("https://api.tomtom.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeoAPI::class.java)

    @Singleton
    @Provides
    fun provideAqiApi(): AqiAPI = Retrofit.Builder()
        .baseUrl("https://air-quality-api.open-meteo.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AqiAPI::class.java)

}