package com.sidukov.weatherapp.ui.fragment_weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.domain.CurrentWeather
import com.sidukov.weatherapp.domain.WeatherDescription
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

    private val _angleStateFlow = MutableStateFlow(Float.NaN)
    var angleStateFlow = _angleStateFlow.asStateFlow()

    private val _todayCardViewDescription = MutableStateFlow<List<WeatherDescription>>(emptyList())
    var todayCardViewDescription = _todayCardViewDescription.asStateFlow()

    init {
        viewModelScope.launch {
            val value = repository.getDailyForecast()
            _dailyStateFlow.tryEmit(value.first)
            if (value.second.isNaN()) return@launch
            _angleStateFlow.tryEmit(value.second)
        }

        viewModelScope.launch {
            val value = repository.getCurrentDayForecast(" ")

            _todayStateFlow.emit(value.first)

            if (value.second.isEmpty()) return@launch
            _hourlyStateFlow.emit(value.second)

            _todayCardViewDescription.emit(value.third)

        }
    }
}