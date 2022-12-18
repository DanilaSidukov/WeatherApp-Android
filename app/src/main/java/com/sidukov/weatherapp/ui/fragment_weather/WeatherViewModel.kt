package com.sidukov.weatherapp.ui.fragment_weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.domain.CurrentWeather
import com.sidukov.weatherapp.domain.WeatherShort
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

open class WeatherViewModel(
    private val repository: WeatherRepository,
) : ViewModel() {


    private val _todayStateFlow = MutableSharedFlow<CurrentWeather>()
    var todayStateFlow = _todayStateFlow.asSharedFlow()

    private val _hourlyStateFlow = MutableStateFlow<List<WeatherShort>>(emptyList())
    var hourlyStateFlow = _hourlyStateFlow.asStateFlow()

    private val _dailyStateFlow = MutableStateFlow<List<WeatherShort>>(emptyList())
    var dailyStateFlow = _dailyStateFlow.asStateFlow()


    init {
        viewModelScope.launch {
            val value = repository.getDailyForecast()
            _dailyStateFlow.emit(value.first)
            _todayStateFlow.emit(
                _todayStateFlow.first().copy(
                    arcAngle = value.second
                )
            )
        }

        viewModelScope.launch {
            val value = repository.getCurrentDayForecast()

            _todayStateFlow.emit(value.first)

            if (value.second.isEmpty()) return@launch
            _hourlyStateFlow.emit(value.second)

        }
    }
}