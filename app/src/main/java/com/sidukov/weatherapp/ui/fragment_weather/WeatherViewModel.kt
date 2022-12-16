package com.sidukov.weatherapp.ui.fragment_weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidukov.weatherapp.domain.CurrentWeather
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.domain.HourlyWeather
import com.sidukov.weatherapp.domain.WeatherDescription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class WeatherViewModel(
    private val repository: WeatherRepository,
): ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    var uiStateFlow = _uiStateFlow.asStateFlow()

    init {
//        viewModelScope.launch {
//            val value = repository.getForecast()
//            _uiStateFlow.value = _uiStateFlow.value.copy(
//                weatherDescriptionList = value
//            )
//        }
        viewModelScope.launch {
            val value = repository.getCurrentDayForecast()
            _uiStateFlow.value = _uiStateFlow.value.copy(
                currentDay = value.first,
                hourlyCurrentWeatherData = value.second
            )
        }
        viewModelScope.launch {
//            val value =
        }
    }

    data class UiState(
        val currentDay: List <CurrentWeather> = emptyList(),
        val hourlyCurrentWeatherData: List<HourlyWeather> = emptyList(),
        val dailyWeatherData: List<WeatherDescription> = emptyList()
    )
}