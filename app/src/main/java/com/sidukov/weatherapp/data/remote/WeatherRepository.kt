package com.sidukov.weatherapp.data.remote

import android.content.Context
import android.location.Geocoder
import androidx.core.text.htmlEncode
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.TimezoneMapper
import com.sidukov.weatherapp.data.remote.api.GeoAPI
import com.sidukov.weatherapp.data.remote.api.WeatherAPI
import com.sidukov.weatherapp.domain.Weather
import com.sidukov.weatherapp.domain.WeatherDescription
import com.sidukov.weatherapp.domain.daily_body.DailyForecastRequestBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

//WeatherRepository - получает и возвращает данные
class WeatherRepository(
    private val weatherApi: WeatherAPI,
    private val context: Context,
    private val geoAPI: GeoAPI
) {

    suspend fun getCurrentDayForecast(): List<Weather> {

        val geocodingData = geoAPI.geoData(
            city = "Yoshkar-Ola, Russia".htmlEncode()
        )

        val requestBody = DailyForecastRequestBody(
            geocodingData.results[0].geometry.latitude,
            geocodingData.results[0].geometry.longitude,
            geocodingData.results[0].components.country_code,
            LocalDateTime.now(),
            LocalDateTime.now(),
            hourly = "temperature_2m,relativehumidity_2m,rain,snowfall,cloudcover_mid",
            true
        )

        val timeZone = TimezoneMapper.latLngToTimezoneString(requestBody.latitude, requestBody.longitude)

        val b = weatherApi.currentDayForecast(
            latitude = requestBody.latitude,
            longitude = requestBody.longitude,
            hourly = requestBody.hourly,
            timezone = timeZone.toString(),
            currentWeather = true,
            startDate = requestBody.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            endDate = requestBody.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        )

        val position: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val rainSizeList = b.hourly.rain
        val snowfallSizeList = b.hourly.snowfall
        val cloudList = b.hourly.cloudCover
        val humidity = b.hourly.humidity[position]
        val headerImage =
            getImageByData(rainSizeList[position], snowfallSizeList[position], cloudList[position], b.currentWeather.temperature)

        val location = getAddress(b.latitude, b.longitude)
        val currentTemperature = b.currentWeather.temperature

        return listOf(
            Weather(
                date = location,
                image = headerImage,
                temperature = currentTemperature.toInt(),
                humidity = humidity.toInt()
            )
        )
    }

    private fun getImageByData(rainValue: Float, snowValue: Float, cloudCover: Float, temperature: Float): Int {
        var imageId: Int = 0
        val precipitationCloud: Float = 40.0f
        val precipitationValue: Float = 0.4f
        val temperatureValue: Float = -3f
        imageId = if (rainValue >= precipitationValue) {
            return R.drawable.ic_sky_rainy_light
        } else if (snowValue >= precipitationValue && cloudCover >= precipitationCloud) {
            return R.drawable.ic_sky_snow_light
        } else if (snowValue >= precipitationValue && temperatureValue > temperature || temperatureValue > temperature) {
            return R.drawable.ic_snowflake
        } else if (cloudCover >= precipitationCloud) {
            return R.drawable.ic_sky_light
        } else {
            R.drawable.ic_sun
        }
        return imageId
    }

    private var geocoder: Geocoder = Geocoder(context, Locale.getDefault())

    private fun getAddress(latitude: Float, longitude: Float): String {
        val address = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
        return address?.get(0)?.locality + ", " + address?.get(0)?.countryName
    }

    fun getWeatherDetails(): List<WeatherDescription> {
        return listOf(
            WeatherDescription(
                R.string.air_quality,
                "Good",
                R.id.progress_bar,
                R.drawable.ic_air_quality
            ),
            WeatherDescription(
                R.string.aqi,
                "72",
                R.id.progress_bar,
                R.drawable.ic_air_quality
            ),
            WeatherDescription(
                R.string.humidity,
                "43",
                R.id.progress_bar,
                R.drawable.ic_humidity
            ),
            WeatherDescription(
                R.string.precipitation,
                "3",
                R.id.progress_bar,
                R.drawable.ic_sky_rainy_dark
            )
        )
    }

}