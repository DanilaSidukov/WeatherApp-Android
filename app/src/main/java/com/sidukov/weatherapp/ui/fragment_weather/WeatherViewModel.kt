package com.sidukov.weatherapp.ui.fragment_weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidukov.weatherapp.domain.Weather
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.domain.WeatherDescription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class WeatherViewModel(
    private val repository: WeatherRepository,
): ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    var uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val value = repository.getForecast()
            _uiStateFlow.value = _uiStateFlow.value.copy(
                weatherDescriptionList = value
            )
        }
        viewModelScope.launch {
            val value = repository.getCurrentDayForecast()
            _uiStateFlow.value = _uiStateFlow.value.copy(
                weatherList = value
            )
        }
    }

    data class UiState(
        val weatherList: List<Weather> = emptyList(),
        val weatherDescriptionList: List<WeatherDescription> = emptyList()
    )
}