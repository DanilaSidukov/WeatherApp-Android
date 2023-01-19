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
import javax.inject.Inject

open class LocationViewModel @Inject constructor(
    val repositoryLocation: LocationRepository,
    val weatherRepository: WeatherRepository,
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

    fun deleteItem(locationItem: EntityLocation) {
        viewModelScope.launch {
            repositoryLocation.deleteLocationById(locationItem.name)
            _locationList.value = repositoryLocation.getLocationData()
            println("list delete = ${_locationList.value}")
        }
    }

    fun setDefaultLocation(newLocation: String) {
        viewModelScope.launch {
            repositoryLocation.setSavedLocation(newLocation)
        }
    }
}