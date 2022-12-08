package com.sidukov.weatherapp.data.remote

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.remote.api.WeatherAPI
import com.sidukov.weatherapp.domain.Weather
import com.sidukov.weatherapp.domain.WeatherDescription
import com.sidukov.weatherapp.domain.daily_body.CurrentWeather
import com.sidukov.weatherapp.domain.daily_body.DailyForecastRequestBody
import java.time.format.DateTimeFormatter
import java.util.*

class WeatherRepository(
    private val weatherApi: WeatherAPI,
    private val context: Context
) {
    //WeatherRepository - получает и возвращает данныеz

    suspend fun getCurrentDayForecast(body: DailyForecastRequestBody): List <Weather> {

        val b =  weatherApi.currentDayForecast(
            latitude =  body.latitude,
            longitude = body.longitude,
            hourly = body.hourly,
            timezone = body.timezone,
            currentWeather = body.currentWeather,
            startDate = body.startDate.format(DateTimeFormatter.ofPattern("yyyy-mm-dd")),
            endDate = body.endDate.format(DateTimeFormatter.ofPattern("yyyy-mm-dd"))
        )

        val position: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val rainSizeList = b.hourly.rain
        val snowfallSizeList = b.hourly.snowfall
        val cloudList = b.hourly.cloudCover
        val humidity = b.hourly.humidity[position]
        val headerImage = getImageByData(rainSizeList[position], snowfallSizeList[position], cloudList[position])

        val location = getAddress(b.latitude, b.longitude)
        val currentTemperature = b.currentWeather.temperature

        return listOf(
            Weather(
            date = location,
            image = headerImage,
            temperature = currentTemperature.toInt(),
            humidity = humidity
            )
        )

    }

    private fun getImageByData(rainValue: Float, snowValue: Float, cloudCover: Float): Int{
        var imageId: Int = 0
        val precipitationCloud: Float = 40.0f
        val precipitationValue: Float = 0.4f
        imageId = if (rainValue >= precipitationValue){
            R.drawable.ic_sky_rainy_light
        } else if (snowValue >= precipitationValue && cloudCover >= precipitationCloud){
            R.drawable.ic_sky_snow_light
        } else if (snowValue >= precipitationValue){
            R.drawable.ic_snowflake
        } else if (cloudCover >= precipitationCloud){
            R.drawable.ic_sky_light
        } else {
            R.drawable.ic_sun
        }
        return imageId
    }

    private var geocoder: Geocoder = Geocoder(context, Locale.getDefault())

    private fun getAddress(latitude: Float, longitude: Float): String{
        val address = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
        return address?.get(0)?.locality + ", " + address?.get(0)?.countryName
    }

    fun getForecast(): List<Weather> {
        return listOf(
            // вместо контекста передавать просто ссылку на ресурс, в твоём случае случае это просто int
            // а затем извлекать оттуда данные уже с каким-то конкретным контекстом.
            // например, в адаптере с помощью holder.[any_view].context.getString()
            Weather(context.getString(R.string.monday), R.drawable.ic_sun, 20),
            Weather(context.getString(R.string.tuesday), R.drawable.ic_sky_dark, 25),
            Weather(context.getString(R.string.wednesday), R.drawable.ic_sky_dark, 19),
            Weather(context.getString(R.string.thursday), R.drawable.ic_sky_dark, 15),
            Weather(context.getString(R.string.friday), R.drawable.ic_sky_dark, 2),
            Weather(context.getString(R.string.saturday), R.drawable.ic_sky_dark, 9),
            Weather(context.getString(R.string.sunday), R.drawable.ic_sky_dark, 13),
            Weather(context.getString(R.string.tuesday), R.drawable.ic_sky_dark, 25),
            Weather(context.getString(R.string.wednesday), R.drawable.ic_sky_dark, 19),
            Weather(context.getString(R.string.thursday), R.drawable.ic_sky_dark, 15),
            Weather(context.getString(R.string.friday), R.drawable.ic_sky_dark, 2),
            Weather(context.getString(R.string.saturday), R.drawable.ic_sky_dark, 9),
            Weather(context.getString(R.string.sunday), R.drawable.ic_sky_dark, 13)
        )
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