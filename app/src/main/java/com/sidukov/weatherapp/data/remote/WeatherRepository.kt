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
import java.util.*

//WeatherRepository - получает и возвращает данные
class WeatherRepository(
    private val weatherApi: WeatherAPI,
    private val geocoder: Geocoder,
    private val geoAPI: GeoAPI
) {

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
            hourly = "temperature_2m,relativehumidity_2m,precipitation,rain,snowfall,weathercode,cloudcover_mid",
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
                weatherTodayData.hourly.hourlyWeatherCode[position]
            ),
            temperature = weatherTodayData.hourly.temperature[position].toInt(),
            humidity = weatherTodayData.hourly.humidity[position].toInt(),
            description = DescriptionToday.valueFromRange(weatherTodayData.currentWeather.weathercode).value,
            currentWeatherCode = weatherTodayData.hourly.hourlyWeatherCode[0],
            precipitation = weatherTodayData.hourly.precipitation[position],
            dayTimeDigest = DescriptionDigest.valueFromRange(weatherTodayData.hourly.hourlyWeatherCode[13]).value,
            nightTimeDigest = DescriptionDigest.valueFromRange (weatherTodayData.hourly.hourlyWeatherCode[22]).value
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
                        weatherTodayData.hourly.hourlyWeatherCode[hour]
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

    private fun getImageByData(code: Int): Pair<Int, Int> {

        if (code == 0) return Pair(R.drawable.ic_sun, R.drawable.ic_sun)
        else if (code in 1..3 || code in 45..48) return Pair(
            R.drawable.ic_sun,
            R.drawable.ic_sky_light
        )
        else if (code in 51..67 || code in 80..82) return Pair(
            R.drawable.ic_sky_rainy_light,
            R.drawable.ic_sky_rainy_light
        )
        else if (code == 71) return Pair(R.drawable.ic_snowflake, R.drawable.ic_snowflake)
        else if (code in 73..77 || code in 85..86) return Pair(
            R.drawable.ic_sky_snow_light,
            R.drawable.ic_sky_snow_light
        )
        else return Pair(R.drawable.ic_sky_rainy_dark, R.drawable.ic_sky_rainy_dark)
    }

    enum class DescriptionToday(val wc: IntRange, val value: Int) {
        Clear(0..0, R.string.clear),
        MainlyClear(1..3, R.string.mainly_clear),
        Fog(45..48, R.string.fog),
        Drizzle(51..55, R.string.drizzle),
        FreezingDrizzle(56..57, R.string.freezing_drizzle),
        Rain(61..65, R.string.rain),
        FreezingRain(66..67, R.string.freezing_rain),
        SnowFall(71..75, R.string.snow_fall),
        SnowGrains(77..77, R.string.snow_grains),
        RainShowers(80..82, R.string.rain_showers),
        SnowShowers(85..86, R.string.snow_showers),
        Thunderstorm(95..95, R.string.thunderstorm),
        ThunderstormRain(96..99, R.string.thunderstorm_rain),
        Error(IntRange.EMPTY, R.string.error);

        companion object {
            fun valueFromRange(num: Int): DescriptionToday {
                return values().firstOrNull { num in it.wc } ?: Error
            }
        }
    }

    enum class DescriptionDigest(val wc: IntRange, val value: Int) {
        ClearDigest(0..0, R.string.clear_digest),
        MainlyClearDigest(1..3, R.string.mainly_clear_digest),
        FogDigest(45..48, R.string.fog_digest),
        DrizzleDigest(51..55, R.string.drizzle_digest),
        FreezingDrizzleDigest(56..57, R.string.freezing_drizzle_digest),
        RainDigest(61..65, R.string.rain_digest),
        FreezingRainDigest(66..67, R.string.freezing_rain_digest),
        SnowFallDigest(71..75, R.string.snow_fall_digest),
        SnowGrainsDigest(77..77, R.string.snow_grains_digest),
        RainShowersDigest(80..82, R.string.rain_showers_digest),
        SnowShowersDigest(85..86, R.string.snow_showers_digest),
        ThunderstormDigest(95..95, R.string.thunderstorm_digest),
        ThunderstormRainDigest(96..99, R.string.thunderstorm_rain_digest),
        ErrorDigest(IntRange.EMPTY, R.string.error_digest);

        companion object{
            fun valueFromRange(num: Int): DescriptionDigest {
                return values().firstOrNull { num in it.wc } ?: ErrorDigest
            }
        }
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

    private fun getHourlyImageByData(code: Int): Int {
        if (code == 0) return R.drawable.ic_sun
        else if (code in 1..3 || code in 45..48) return R.drawable.ic_sky_with_sun_light
        else if (code in 51..67 || code in 80..82) return R.drawable.ic_sky_rainy_light
        else if (code == 71) return R.drawable.ic_snowflake
        else if (code in 73..77 || code in 85..86) return R.drawable.ic_sky_snow_light
        else return R.drawable.ic_sky_rainy_dark
    }

    private fun convertSunRiseOrSet(sunData: String): String {
        val str = sunData.toCharArray()
        var tempString = ""
        (11..15).map {
            tempString += str[it]
        }
        return tempString
    }

    private fun getSweepAngle(rise: String, set: String): Float {
        val sunRiseMinute = LocalDateTime.parse(rise).toEpochSecond(ZoneOffset.UTC)
        val sunSetMinute = LocalDateTime.parse(set).toEpochSecond(ZoneOffset.UTC)
        val nowMinute = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

        return if (nowMinute <= sunRiseMinute)
            0f
        else if (nowMinute in sunRiseMinute..sunSetMinute)
                ((nowMinute - sunRiseMinute) / ((sunSetMinute - sunRiseMinute) / 140f))
        else
            140f
    }

}