package com.sidukov.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sidukov.weatherapp.data.Weather
import com.sidukov.weatherapp.data.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

open class WeatherViewModel(
    private val repository: WeatherRepository
): ViewModel() {
    //Поле для хранения данных о погоде, оно обновляется внутри WeatherViewModel
    private val _weatherList = MutableStateFlow<List<Weather>>(mutableListOf())
    //Поле для получения данных фрагментом, это для того, чтобы можно было из него только получать данные
    val weatherList = _weatherList.asSharedFlow()
    //viewModelScope встроен в класс ViewModel, launch запускает Корутину, привязанную к жизненному циклу vm
    init {
        viewModelScope.launch {
            _weatherList.value = repository.getForecast()
        }
    }

}