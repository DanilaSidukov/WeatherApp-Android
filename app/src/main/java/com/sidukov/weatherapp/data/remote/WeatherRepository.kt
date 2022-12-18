package com.sidukov.weatherapp.data.remote

import android.location.Geocoder
import androidx.core.text.htmlEncode
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.TimezoneMapper
import com.sidukov.weatherapp.data.remote.api.GeoAPI
import com.sidukov.weatherapp.data.remote.api.WeatherAPI
import com.sidukov.weatherapp.domain.CurrentWeather
import com.sidukov.weatherapp.domain.WeatherDescription
import com.sidukov.weatherapp.domain.WeatherShort
import com.sidukov.weatherapp.domain.daily_body.DailyForecastRequestBody
import com.sidukov.weatherapp.domain.today_body.TodayForecastRequestBody
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.*

//WeatherRepository - получает и возвращает данные
class WeatherRepository(
    private val weatherApi: WeatherAPI,
    private val geocoder: Geocoder,
    private val geoAPI: GeoAPI
) {

    private lateinit var currentCondition: DescriptionCondition

    private lateinit var tempListHours: List<WeatherShort>

    private lateinit var tempListDays: List<WeatherShort>

    suspend fun getCurrentDayForecast(): Pair<CurrentWeather, List<WeatherShort>> {

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
        val weatherTodayData = weatherApi.currentDayForecast(
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
        val rainSizeList = weatherTodayData.hourly.rain
        val snowfallSizeList = weatherTodayData.hourly.snowfall
        val cloudList = weatherTodayData.hourly.cloudCover
        // извлекаем влажность из списка, там 24 элемента, по позиции, определённой выше. то есть по индексу position
        val location = getAddress(weatherTodayData.latitude, weatherTodayData.longitude)

        // все данные о погоде по текущему часу
        val currentWeatherCurrentData = CurrentWeather(
            date = location,
            imageMain = getImageByData(
                rainSizeList[position],
                snowfallSizeList[position],
                cloudList[position],
                weatherTodayData.hourly.temperature[position]
            ),
            temperature = weatherTodayData.hourly.temperature[position].toInt(),
            humidity = weatherTodayData.hourly.humidity[position].toInt(),
            description = getDescription(weatherTodayData.currentWeather.weathercode).value
        )

        var weatherShortList: List<WeatherShort> = emptyList()
        var tempString = ""

        (0..23).map { hour ->
            tempString = if (hour < 10) {
                "0$hour:00"
            } else "$hour:00"
            tempListHours = listOf(
                WeatherShort(
                    hour = tempString,
                    image = getHourlyImageByData(
                        rainSizeList[hour],
                        snowfallSizeList[hour],
                        cloudList[hour],
                        weatherTodayData.hourly.temperature[hour]
                    ),
                    temperature = weatherTodayData.hourly.temperature[hour].toInt(),
                    " ",
                    " "
                )
            )
            weatherShortList = weatherShortList.plus(tempListHours)
        }

        return Pair(currentWeatherCurrentData, weatherShortList)
    }

    suspend fun getDailyForecast(): Pair<List<WeatherShort>, Float> {
        val geocodingData = geoAPI.geoData(
            city = "Yoshkar-Ola, Russia".htmlEncode()
        )
        val requestBody = DailyForecastRequestBody(
            latitude = geocodingData.results[0].geometry.latitude,
            longitude = geocodingData.results[0].geometry.longitude,
            timezone = geocodingData.results[0].components.country_code,
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusWeeks(2),
            daily = "weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset"
        )

        val timeZone =
            TimezoneMapper.latLngToTimezoneString(requestBody.latitude, requestBody.longitude)

        val weatherDailyData = weatherApi.weeklyDailyForecast(
            latitude = requestBody.latitude,
            longitude = requestBody.longitude,
            daily = requestBody.daily,
            timezone = timeZone.toString(),
            startDate = requestBody.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            endDate = requestBody.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        )

        val days: Map<Int, String> = mapOf(
            1 to "MONDAY",
            2 to "TUESDAY",
            3 to "WEDNESDAY",
            4 to "THURSDAY",
            5 to "FRIDAY",
            6 to "SATURDAY",
            7 to "SUNDAY"
        )

        var dailyWeatherList: List<WeatherShort> = emptyList()
        val templistValuesDays = days.values
        println("$templistValuesDays")
        var beginDayOfWeek = ""

        beginDayOfWeek = templistValuesDays.first { item ->
            item.uppercase() == LocalDateTime.now().dayOfWeek.toString()
        }.toString()

        var indexDaysOfWeek = days.filterValues { it == beginDayOfWeek }.keys.toList()[0]

        (0..14).map { day ->
            tempListDays = listOf(
                WeatherShort(
                    hour = days[indexDaysOfWeek].toString(),
                    image = getImageByWeatherCode(weatherDailyData.daily.weathercode[day]),
                    temperature = getDailyTemperature(
                        weatherDailyData.daily.temperature_max[day],
                        weatherDailyData.daily.temperature_min[day]
                    ),
                    sunrise = convertSunRiseOrSet(weatherDailyData.daily.sunrise[day]),
                    sunset = convertSunRiseOrSet(weatherDailyData.daily.sunset[day])
                )
            )
            if (indexDaysOfWeek > 6) indexDaysOfWeek = 1
            else indexDaysOfWeek += 1

            dailyWeatherList = dailyWeatherList.plus(tempListDays)
        }
        return Pair(
            dailyWeatherList,
            getSweepAngle(weatherDailyData.daily.sunrise[0], weatherDailyData.daily.sunset[0])
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

    private fun getDescription(value: Int): DescriptionCondition {
        if (value == 0) currentCondition = DescriptionCondition.Clear
        else if (value in 1..3) currentCondition = DescriptionCondition.MainlyClear
        else if (value in 45..48) currentCondition = DescriptionCondition.Fog
        else if (value in 51..55) currentCondition = DescriptionCondition.Drizzle
        else if (value in 56..57) currentCondition = DescriptionCondition.FreezingDrizzle
        else if (value in 61..65) currentCondition = DescriptionCondition.Rain
        else if (value in 66..67) currentCondition = DescriptionCondition.FreezingRain
        else if (value in 71..75) currentCondition = DescriptionCondition.SnowFall
        else if (value == 77) currentCondition = DescriptionCondition.SnowGrains
        else if (value in 80..82) currentCondition = DescriptionCondition.RainShowers
        else if (value in 85..86) currentCondition = DescriptionCondition.SnowShowers
        else if (value == 95) currentCondition = DescriptionCondition.Thunderstorm
        else if (value in 96..99) currentCondition = DescriptionCondition.ThunderstormRain
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

    private fun getImageByWeatherCode(code: Int): Int {
        if (code == 0) return R.drawable.ic_sun
        else if (code in 1..3 || code in 45..48) return R.drawable.ic_sky_with_sun_light
        else if (code in 51..67 || code in 80..82) return R.drawable.ic_sky_rainy_light
        else if (code == 71) return R.drawable.ic_snowflake
        else if (code in 73..77 || code in 85..86) return R.drawable.ic_sky_snow_light
        else return R.drawable.ic_sky_rainy_dark
    }

    private fun getDailyTemperature(max: Float, min: Float): Int {
        val temperature = (max + min) / 2
        return temperature.toInt()
    }

    private fun getHourlyImageByData(
        rainValue: Float,
        snowValue: Float,
        cloudCover: Float,
        temperature: Float
    ): Int {
        val precipitationCloud = 40.0f
        val precipitationValue = 0.4f
        val temperatureValue: Float = -3f
        if (rainValue >= precipitationValue && rainValue > snowValue) {
            return R.drawable.ic_sky_rainy_light
        } else if (snowValue >= precipitationValue && snowValue > rainValue && cloudCover >= precipitationCloud) {
            return R.drawable.ic_sky_snow_light
        } else if (temperature < temperatureValue && snowValue < precipitationValue) {
            return R.drawable.ic_snowflake
        } else if (snowValue < precipitationValue && temperature > temperatureValue && cloudCover > precipitationCloud) {
            return R.drawable.ic_sky_with_sun_light
        } else if (temperature > temperatureValue && snowValue < precipitationValue && cloudCover < precipitationCloud) {
            return R.drawable.ic_sun
        } else {
            currentCondition = DescriptionCondition.Error
            return 0
        }
    }

    private fun convertSunRiseOrSet(sunData: String): String {
        val str = sunData.toCharArray()
        var tempString = ""
        (11..15).map {
            tempString += str[it]
        }
        return tempString
    }

    fun getSweepAngle(rise: String, set: String): Float {
        val sunRiseMinute =
            LocalDateTime.parse(rise).toEpochSecond(ZoneOffset.UTC) * 1000 + LocalDateTime.parse(
                rise
            ).get(
                ChronoField.MINUTE_OF_DAY
            )
        println("RISE = $sunRiseMinute")
        val sunSetMinute =
            LocalDateTime.parse(set).toEpochSecond(ZoneOffset.UTC) * 1000 + LocalDateTime.parse(set)
                .get(
                    ChronoField.MINUTE_OF_DAY
                )
        println("SET = $sunSetMinute")
        val nowMinute =
            LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000 + LocalDateTime.now().get(
                ChronoField.MINUTE_OF_DAY
            )
        println("NOW = $nowMinute")

        var sunValue = 0f

        if (nowMinute < sunRiseMinute) sunValue = 0f
        if (nowMinute > sunSetMinute) sunValue = 140f
        if (nowMinute in (sunRiseMinute + 1) until sunSetMinute) {
            val onePart = sunSetMinute / 140
            sunValue = (onePart * nowMinute).toFloat()
            println("PROGRESS = ${sunValue}")
            return sunValue
        }
        return sunValue
    }

}