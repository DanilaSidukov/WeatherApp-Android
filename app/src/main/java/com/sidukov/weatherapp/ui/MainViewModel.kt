package com.sidukov.weatherapp.ui

import androidx.lifecycle.ViewModel
import com.sidukov.weatherapp.data.remote.LocationRepository
import javax.inject.Inject

open class MainViewModel @Inject constructor(
    var locationRepository: LocationRepository
): ViewModel()