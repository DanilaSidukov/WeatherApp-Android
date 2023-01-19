package com.sidukov.weatherapp.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import javax.inject.Qualifier
import kotlin.reflect.KClass

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Weather

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Geo

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Aqi

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
