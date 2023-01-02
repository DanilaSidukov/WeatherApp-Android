package com.sidukov.weatherapp.ui.fragment_location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidukov.weatherapp.data.local.EntityLocation
import com.sidukov.weatherapp.data.remote.LocationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    private val repositoryLocation: LocationRepository,
): ViewModel(){

    private var _locationList = MutableStateFlow<List<EntityLocation>>(mutableListOf())
    val locationList = _locationList.asSharedFlow()

    var canBeAdded = MutableSharedFlow<Boolean>()

    var deleteItem = MutableSharedFlow<Unit>()

    init {
        viewModelScope.launch {
            _locationList.value = repositoryLocation.getLocationData()
//            deleteItem.emit(repositoryLocation.deleteAllLocation())
        }
    }

    fun deleteItem(){
        viewModelScope.launch {
            deleteItem.emit(
                repositoryLocation.deleteLocationData()
            )
        }
    }

    fun addLocation() {
//        if (_locationList.value)
//        canBeAdded = true
//        canBeAdded = false
    }

}