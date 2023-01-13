package com.sidukov.weatherapp.ui.fragment_weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidukov.weatherapp.data.local.EntityLocation
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.domain.CurrentWeather
import com.sidukov.weatherapp.domain.WeatherDescription
import com.sidukov.weatherapp.domain.WeatherShort
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class WeatherViewModel(
    private val repository: WeatherRepository,
    private val cityName: String
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

    private val _listToLocationFragment = MutableSharedFlow<EntityLocation>()
    var listToLocationFragment = _listToLocationFragment.asSharedFlow()

    private val index = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH")).toInt()

    init {
        viewModelScope.launch {

            val value = repository.getCurrentDayForecast(cityName)
            if (value.second.isEmpty() || value.third.isEmpty()) return@launch
            _todayStateFlow.emit(value.first)
            _hourlyStateFlow.emit(value.second)
            _todayCardViewDescription.emit(value.third)
            if (value.fifth.isNaN() || value.fourth.isEmpty()) return@launch
            _dailyStateFlow.tryEmit(value.fourth)
            _angleStateFlow.tryEmit(value.fifth)

            val entityLocation = EntityLocation(
                name = value.first.date,
                date =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM")),
                temperature = value.first.temperature,
                image = value.second[index].image
            )
            println("entity in view model = $entityLocation")
            _listToLocationFragment.emit(entityLocation)
        }
    }

}