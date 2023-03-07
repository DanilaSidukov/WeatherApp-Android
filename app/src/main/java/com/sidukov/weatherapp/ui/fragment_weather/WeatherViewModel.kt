package com.sidukov.weatherapp.ui.fragment_weather

import android.content.Context
import android.net.ConnectivityManager
import android.net.InetAddresses
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidukov.weatherapp.data.local.db.EntityLocation
import com.sidukov.weatherapp.data.remote.LocationRepository
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.domain.CurrentWeather
import com.sidukov.weatherapp.domain.WeatherDescription
import com.sidukov.weatherapp.domain.WeatherShort
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.InetAddress.getByName
import java.net.NetworkInterface.getByName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


open class WeatherViewModel @Inject constructor(
    val weatherRepository: WeatherRepository,
    val locationRepository: LocationRepository,
    val context: Context,
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

            if (isNetworkConnected()) {
                val value =
                    weatherRepository.getCurrentDayForecast(locationRepository.settings.savedLocation
                        ?: " ")
                if (value.second.isEmpty() || value.third.isEmpty()) return@launch
                _todayStateFlow.emit(value.first)
                _hourlyStateFlow.emit(value.second)
                _todayCardViewDescription.emit(value.third)
                if (value.fifth.isNaN() || value.fourth.isEmpty()) return@launch
                _dailyStateFlow.tryEmit(value.fourth)
                _angleStateFlow.tryEmit(value.fifth)
            } else {
                Toast.makeText(context, "Connection error! Please, check your internet connection", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        capabilities?.let {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true
        }
        return false
    }

}