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
    private val geocoder: Geocoder,
    private val geoAPI: GeoAPI
) {

    private lateinit var currentCondition: DescriptionCondition

    suspend fun getCurrentDayForecast(): List<Weather> {

        val geocodingData = geoAPI.geoData(
            city = "Yoshkar-Ola, Russia".htmlEncode()
        )


        // Это мы создаём объект с данными, которые передадим в API запрос
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

        // здесь мы передаём данные, которые создали выше, и получаем список элементов
        val b = weatherApi.currentDayForecast(
            latitude = requestBody.latitude,
            longitude = requestBody.longitude,
            hourly = requestBody.hourly,
            timezone = timeZone.toString(),
            currentWeather = true,
            startDate = requestBody.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            endDate = requestBody.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        )

        // здесь мы находим текущий час, он совпадает с индексом элемента в пришедшем с серверва списке
        val position: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val rainSizeList = b.hourly.rain
        val snowfallSizeList = b.hourly.snowfall
        val cloudList = b.hourly.cloudCover
        // извлекаем влажность из списка, там 24 элемента, по позиции, определённой выше. то есть по индексу position
        val humidity = b.hourly.humidity[position]
        // определяем картинку
        val headerImage =
            getImageByData(rainSizeList[position], snowfallSizeList[position], cloudList[position], b.currentWeather.temperature)
        val location = getAddress(b.latitude, b.longitude)
        val currentTemperature = b.currentWeather.temperature


        return (0..23).map { i ->
            Weather(
                location,
                getImageByData(rainSizeList[i], snowfallSizeList[i], cloudList[i], b.hourly.temperature[i]),
                b.hourly.temperature[i].toInt(),
                b.hourly.humidity[i].toInt(),
                currentCondition.value
            )
        }

        // возвращаем список из одного элемента, мы этом элементе данные только по текущему часу
//        return listOf(
//            Weather(
//                date = location,
//                imageMain = headerImage,
//                temperature = currentTemperature.toInt(),
//                humidity = humidity.toInt(),
//                description = currentCondition.value
//            )
//        )
    }

    private fun getImageByData(rainValue: Float, snowValue: Float, cloudCover: Float, temperature: Float): Pair<Int, Int> {
        val imageId : Pair <Int, Int> = Pair(0, 1)
        val precipitationCloud = 40.0f
        val precipitationValue = 0.4f
        val temperatureValue: Float = -3f
        if (rainValue >= precipitationValue && rainValue > snowValue) {
            currentCondition = DescriptionCondition.Rainy
            return imageId.copy(first = R.drawable.ic_sky_rainy_light, second = R.drawable.ic_sky_rainy_light)
        } else if (snowValue >= precipitationValue && snowValue > rainValue && cloudCover >= precipitationCloud) {
            currentCondition = DescriptionCondition.Snowfall
            return imageId.copy(first = R.drawable.ic_sky_snow_light, second = R.drawable.ic_sky_snow_light)
        } else if (temperature < temperatureValue && snowValue < precipitationValue) {
            currentCondition = DescriptionCondition.Cold
            return imageId.copy(first = R.drawable.ic_snowflake, second = R.drawable.ic_snowflake)
        } else if (snowValue < precipitationValue && temperature > temperatureValue && cloudCover > precipitationCloud) {
            currentCondition = DescriptionCondition.CloudCover
            return imageId.copy(first = R.drawable.ic_sun, second = R.drawable.ic_sky_light)
        } else if (temperature > temperatureValue && snowValue < precipitationValue && cloudCover < precipitationCloud)  {
            currentCondition = DescriptionCondition.Sunny
            return imageId.copy (first = R.drawable.ic_sun, second = R.drawable.ic_sun)
        } else {
            currentCondition = DescriptionCondition.Error
            return imageId
        }
    }

    enum class DescriptionCondition(val value: Int) {
        Cold(R.string.cold),
        Sunny(R.string.sunny),
        Rainy(R.string.rainy),
        Error(R.string.error),
        Snowfall(R.string.snowfall),
        CloudCover(R.string.cloud_cover)
    }

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