package com.sidukov.weatherapp.data.remote

import android.location.Geocoder
import androidx.core.text.htmlEncode
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.TimezoneMapper
import com.sidukov.weatherapp.data.remote.api.GeoAPI
import com.sidukov.weatherapp.data.remote.api.WeatherAPI
import com.sidukov.weatherapp.domain.CurrentWeather
import com.sidukov.weatherapp.domain.HourlyWeather
import com.sidukov.weatherapp.domain.WeatherDescription
import com.sidukov.weatherapp.domain.daily_body.DailyForecastRequestBody
import com.sidukov.weatherapp.domain.daily_body.temp_pack.DailyForecastBody
import com.sidukov.weatherapp.domain.today_body.TodayForecastRequestBody
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

    private lateinit var tempList: List<HourlyWeather>

    suspend fun getCurrentDayForecast(): Pair<List<CurrentWeather>, List<HourlyWeather>> {

        val geocodingData = geoAPI.geoData(
            city = "Yoshkar-Ola, Russia".htmlEncode()
        )


        // Это мы создаём объект с данными, которые передадим в API запрос
        val requestBody = TodayForecastRequestBody(
            geocodingData.results[0].geometry.latitude,
            geocodingData.results[0].geometry.longitude,
            geocodingData.results[0].components.country_code,
            LocalDateTime.now(),
            LocalDateTime.now(),
            hourly = "temperature_2m,relativehumidity_2m,rain,snowfall,cloudcover_mid",
            true
        )

        val timeZone =
            TimezoneMapper.latLngToTimezoneString(requestBody.latitude, requestBody.longitude)

        // здесь мы передаём данные, которые создали выше, и получаем список элементов
        val b = weatherApi.currentDayForecast(
            latitude = requestBody.latitude,
            longitude = requestBody.longitude,
            hourly = requestBody.hourly,
            timezone = timeZone.toString(),
            currentWeather = true,
            startDate = requestBody.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            endDate = requestBody.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
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
            getImageByData(
                rainSizeList[position],
                snowfallSizeList[position],
                cloudList[position],
                b.currentWeather.temperature
            )
        val location = getAddress(b.latitude, b.longitude)
        val currentTemperature = b.currentWeather.temperature

        // все данные о погоде по текущему часу
        val currentWeatherCurrentData = listOf(
            CurrentWeather(
                date = location,
                imageMain = getImageByData(
                    rainSizeList[position],
                    snowfallSizeList[position],
                    cloudList[position],
                    b.hourly.temperature[position]
                ),
                temperature = b.hourly.temperature[position].toInt(),
                humidity = b.hourly.humidity[position].toInt(),
                description = getDescription(b.currentWeather.weathercode).value
            )
        )

        var hourlyWeatherList : List <HourlyWeather> = emptyList()
        var tempString = ""

        (0..23).map { hour ->
            tempString = if (hour < 10) {
                "0$hour:00"
            } else "$hour:00"
            tempList = listOf(
                    HourlyWeather(
                        hour = tempString,
                        image = getImageByData(
                            rainSizeList[hour],
                            snowfallSizeList[hour],
                            cloudList[hour],
                            b.hourly.temperature[hour]
                        ),
                        temperature = b.hourly.temperature[hour].toInt()
                    )
                )
            hourlyWeatherList = hourlyWeatherList.plus(tempList)
        }

        return Pair(currentWeatherCurrentData, hourlyWeatherList)
    }

    suspend fun getDailyForecast(): List<DailyForecastBody>{
        val geocodingData = geoAPI.geoData(
            city = "Yoshkar-Ola, Russia".htmlEncode()
        )
        val requestBody = DailyForecastRequestBody(
            latitude = geocodingData.results[0].geometry.latitude,
            longitude = geocodingData.results[0].geometry.longitude,
            timezone = geocodingData.results[0].components.country_code,
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-${LocalDateTime.now().dayOfMonth+14}")),
            daily = "weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset"
        )
    }

    private fun getImageByData(
        rainValue: Float,
        snowValue: Float,
        cloudCover: Float,
        temperature: Float
    ): Pair<Int, Int> {
        val imageId: Pair<Int, Int> = Pair(0, 1)
        val precipitationCloud = 40.0f
        val precipitationValue = 0.4f
        val temperatureValue: Float = -3f
        if (rainValue >= precipitationValue && rainValue > snowValue) {
            return imageId.copy(
                first = R.drawable.ic_sky_rainy_light,
                second = R.drawable.ic_sky_rainy_light
            )
        } else if (snowValue >= precipitationValue && snowValue > rainValue && cloudCover >= precipitationCloud) {
            return imageId.copy(
                first = R.drawable.ic_sky_snow_light,
                second = R.drawable.ic_sky_snow_light
            )
        } else if (temperature < temperatureValue && snowValue < precipitationValue) {
            return imageId.copy(first = R.drawable.ic_snowflake, second = R.drawable.ic_snowflake)
        } else if (snowValue < precipitationValue && temperature > temperatureValue && cloudCover > precipitationCloud) {
            return imageId.copy(first = R.drawable.ic_sun, second = R.drawable.ic_sky_light)
        } else if (temperature > temperatureValue && snowValue < precipitationValue && cloudCover < precipitationCloud) {
            return imageId.copy(first = R.drawable.ic_sun, second = R.drawable.ic_sun)
        } else {
            currentCondition = DescriptionCondition.Error
            return imageId
        }
    }

    enum class DescriptionCondition(val value: Int) {
        Clear(R.string.clear),
        MainlyClear(R.string.mainly_clear),
        Fog(R.string.fog),
        Drizzle(R.string.drizzle),
        FreezingDrizzle(R.string.freezing_drizzle),
        Rain(R.string.rain),
        FreezingRain(R.string.freezing_rain),
        SnowFall(R.string.snow_fall),
        SnowGrains(R.string.snow_grains),
        RainShowers(R.string.rain_showers),
        SnowShowers(R.string.snow_showers),
        Thunderstorm(R.string.thunderstorm),
        ThunderstormRain(R.string.thunderstorm_rain),
        Error(R.string.error),
    }

    private fun getDescription(value: Int): DescriptionCondition{
        if (value == 0) currentCondition = DescriptionCondition.Clear
        else if (value == 1 || value == 2 || value == 3) currentCondition = DescriptionCondition.MainlyClear
        else if (value == 45 || value == 48) currentCondition = DescriptionCondition.Fog
        else if (value == 51 || value == 53 || value == 55) currentCondition = DescriptionCondition.Drizzle
        else if (value == 56 || value == 57 ) currentCondition = DescriptionCondition.FreezingDrizzle
        else if (value == 61 || value == 63 || value == 65) currentCondition = DescriptionCondition.Rain
        else if (value == 66 || value == 67) currentCondition =DescriptionCondition.FreezingRain
        else if (value == 71 || value == 73 || value == 75) currentCondition = DescriptionCondition.SnowFall
        else if (value == 77) currentCondition = DescriptionCondition.SnowGrains
        else if (value ==80 || value == 81 || value == 82) currentCondition = DescriptionCondition.RainShowers
        else if (value == 85 || value == 86) currentCondition = DescriptionCondition.SnowShowers
        else if (value == 95) currentCondition = DescriptionCondition.Thunderstorm
        else if (value == 96 || value == 99) currentCondition = DescriptionCondition.ThunderstormRain
        else currentCondition = DescriptionCondition.Error
        return currentCondition
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