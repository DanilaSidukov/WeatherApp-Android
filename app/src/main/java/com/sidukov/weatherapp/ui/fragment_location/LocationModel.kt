package com.sidukov.weatherapp.ui.fragment_location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidukov.weatherapp.data.Location
import com.sidukov.weatherapp.data.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LocationModel(private val repository: LocationRepository
): ViewModel(){

    private var _locationList = MutableStateFlow<List<Location>>(mutableListOf())

    val locationList = _locationList.asSharedFlow()

    init {
        viewModelScope.launch {
            _locationList.value = repository.getLocationData()
        }
    }

}