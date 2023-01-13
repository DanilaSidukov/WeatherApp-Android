package com.sidukov.weatherapp.ui.fragment_location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidukov.weatherapp.data.local.db.EntityLocation
import com.sidukov.weatherapp.data.remote.LocationRepository
import com.sidukov.weatherapp.data.remote.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocationViewModel(
    private val repositoryLocation: LocationRepository,
    private val weatherRepository: WeatherRepository,
) : ViewModel() {

    private var _locationList = MutableStateFlow<List<EntityLocation>>(emptyList())
    val locationList = _locationList.asStateFlow()

    init {
        getLocationDataBase()
    }

    fun getLocationDataBase() {
        viewModelScope.launch {
            _locationList.emit(
                repositoryLocation.getLocationData()
            )

            val currentHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH")).toInt()

            val newList = _locationList.value.map {
                weatherRepository.getCurrentDayForecast(
                    it.name
                ).let {
                    EntityLocation(
                        name = it.first.date,
                        date = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("dd-MM")),
                        temperature = it.first.temperature,
                        image = it.second[currentHour].image
                    )
                }
            }
            _locationList.emit(
                newList
            )
        }
    }


    fun deleteItem() {
        viewModelScope.launch {
            repositoryLocation.deleteLocationData()
            _locationList.value = repositoryLocation.getLocationData()
        }
    }

    fun setDefaultLocation(newLocation: String) {
        viewModelScope.launch {
            repositoryLocation.setSavedLocation(newLocation)
        }
    }
}