package com.sidukov.weatherapp.data.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.core.text.htmlEncode
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.NTuple5
import com.sidukov.weatherapp.data.TimezoneMapper
import com.sidukov.weatherapp.data.local.db.EntityLocation
import com.sidukov.weatherapp.data.local.db.LocationDao
import com.sidukov.weatherapp.data.remote.api.AqiAPI
import com.sidukov.weatherapp.data.remote.api.GeoAPI
import com.sidukov.weatherapp.data.remote.api.WeatherAPI
import com.sidukov.weatherapp.domain.CurrentWeather
import com.sidukov.weatherapp.domain.WeatherDescription
import com.sidukov.weatherapp.domain.WeatherShort
import com.sidukov.weatherapp.domain.daily_body.DailyForecastRequestBody
import com.sidukov.weatherapp.domain.geo_api.Address
import com.sidukov.weatherapp.domain.today_body.TodayForecastRequestBody
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


class WeatherRepository @Inject constructor (
    val weatherApi: WeatherAPI,
    val geoAPI: GeoAPI,
    val aqiAPI: AqiAPI,
    val locationDao: LocationDao,
    context: Context,
) {

    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("city", Context.MODE_PRIVATE)
    val edit = sharedPreferences.edit()

    private lateinit var tempListHours: List<WeatherShort>

    private lateinit var tempListDays: List<WeatherShort>

    suspend fun getCurrentDayForecast(city: String): NTuple5<CurrentWeather, List<WeatherShort>, List<WeatherDescription>, List<WeatherShort>, Float> {

        val geocodingData = geoAPI.geoData(
            city = city.htmlEncode()
        )

        if (geocodingData.results.isEmpty()) {
            return NTuple5(
                CurrentWeather("Error",
                    Pair(1, 1),
                    0,
                    0,
                    0,
                    0,
                    0f,
                    0,
                    0,
                    0
                ),
                emptyList(), emptyList(),
                emptyList(), 0f,
            )
        }

        edit.clear()
        edit.putString("city", city)
        edit.apply()

        val requestCurrentDayBody = TodayForecastRequestBody(
            geocodingData.results[0].position.latitude,
            geocodingData.results[0].position.longitude,
            geocodingData.results[0].address.countryCode,
            LocalDateTime.now(),
            LocalDateTime.now(),
            hourly = "temperature_2m,relativehumidity_2m,precipitation,rain,snowfall,weathercode,cloudcover_mid",
            true
        )

        val timeZoneCurrent =
            TimezoneMapper.latLngToTimezoneString(requestCurrentDayBody.latitude,
                requestCurrentDayBody.longitude)

        val aqiData = aqiAPI.getCurrentAQI(
            latitude = requestCurrentDayBody.latitude,
            longitude = requestCurrentDayBody.longitude,
            hourly = "european_aqi",
            timezone = timeZoneCurrent.toString(),
            startDate = requestCurrentDayBody.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            endDate = requestCurrentDayBody.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        )

        val weatherTodayData = weatherApi.currentDayForecast(
            latitude = requestCurrentDayBody.latitude,
            longitude = requestCurrentDayBody.longitude,
            hourly = requestCurrentDayBody.hourly,
            timezone = timeZoneCurrent.toString(),
            currentWeather = true,
            startDate = requestCurrentDayBody.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            endDate = requestCurrentDayBody.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        )

        fun checkLocationOnNull(componentList: Address): String {
            var tempString = " "
            if (componentList.town.isNullOrBlank() || componentList.town == " ") tempString = componentList.city
            else tempString = componentList.town
            if (componentList.tertiaryDistrict.isNullOrBlank() && componentList.city.isNullOrBlank()
                ||componentList.tertiaryDistrict.isNullOrBlank() || componentList.city.isNullOrBlank()) tempString =
                geocodingData.results[0].address.town
            if (componentList.city.isNullOrBlank() && componentList.town.isNullOrBlank()
                || componentList.city.isNullOrBlank() || componentList.town.isNullOrBlank()) tempString =
                geocodingData.results[0].address.tertiaryDistrict
            if (componentList.town.isNullOrBlank() && componentList.tertiaryDistrict.isNullOrBlank()
                || componentList.town.isNullOrBlank() || componentList.tertiaryDistrict.isNullOrBlank()) tempString =
                geocodingData.results[0].address.city
            if (tempString == null || tempString == " ") tempString = "Unknown"
            if (componentList.country == "Rossiya") componentList.country = "Russia"
            return "$tempString, ${componentList.country}"
        }

        val position: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val location =
            checkLocationOnNull(geocodingData.results[0].address)
//        + ", " + geocodingData.results[0].address.country

        edit.putString("city", location)

        val currentWeatherCurrentData =
            CurrentWeather(
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
                nightTimeDigest = DescriptionDigest.valueFromRange(weatherTodayData.hourly.hourlyWeatherCode[22]).value,
                currentAQI = DescriptionAQI.getDescriptionAQI(aqiData.AQIHourly.aqiList[position]).value
            )

        var weatherShortList: List<WeatherShort> = emptyList()
        var tempString = ""

        (0..23).map { hour ->
            tempString = if (hour < 10) {
                "0$hour"
            } else "$hour"
            tempListHours = listOf(
                WeatherShort(
                    hour = tempString,
                    image = getImageByWeatherCode(
                        weatherTodayData.hourly.hourlyWeatherCode[hour]
                    ),
                    temperature = weatherTodayData.hourly.temperature[hour].toInt(),
                    " ",
                    " "
                )
            )
            weatherShortList = weatherShortList.plus(tempListHours)
        }

        val weatherDescription = listOf(
            WeatherDescription(
                name = R.string.air_quality,
                information = DescriptionAQI.getDescriptionAQI(aqiData.AQIHourly.aqiList[position]).value.toString(),
                progress = aqiData.AQIHourly.aqiList[position],
                image = R.drawable.ic_air_quality
            ),
            WeatherDescription(
                name = R.string.aqi,
                information = aqiData.AQIHourly.aqiList[position].toString(),
                progress = aqiData.AQIHourly.aqiList[position],
                image = R.drawable.ic_air_quality
            ),
            WeatherDescription(
                name = R.string.humidity,
                information = weatherTodayData.hourly.humidity[position].toInt().toString() + " %",
                progress = weatherTodayData.hourly.humidity[position].toInt(),
                image = R.drawable.ic_humidity
            ),
            WeatherDescription(
                name = R.string.precipitation,
                information = weatherTodayData.hourly.precipitation[position].toInt()
                    .toString() + " %",
                progress = weatherTodayData.hourly.precipitation[position].toInt() * 100,
                image = R.drawable.ic_sky_rainy_dark
            )
        )

        if (geocodingData.results[0].toString() != "") {
            locationDao.insertData(
                EntityLocation(
                    name = location,
                    date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM")),
                    temperature = currentWeatherCurrentData.temperature,
                    image = getImageByWeatherCode(
                        weatherTodayData.hourly.hourlyWeatherCode[position]
                    )
                )
            )
        }

        val requestDailyBody = DailyForecastRequestBody(
            latitude = geocodingData.results[0].position.latitude,
            longitude = geocodingData.results[0].position.longitude,
            timezone = geocodingData.results[0].address.countryCode,
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusWeeks(2),
            daily = "weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset"
        )

        val timeZoneDaily =
            TimezoneMapper.latLngToTimezoneString(requestDailyBody.latitude,
                requestDailyBody.longitude)

        val weatherDailyData = weatherApi.weeklyDailyForecast(
            latitude = requestDailyBody.latitude,
            longitude = requestDailyBody.longitude,
            daily = requestDailyBody.daily,
            timezone = timeZoneDaily.toString(),
            startDate = requestDailyBody.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            endDate = requestDailyBody.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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

        val shortDays: Map<Int, String> = if (Locale.getDefault().getDisplayLanguage() == "русский"){
            mapOf(
                1 to "ПН",
                2 to "ВТ",
                3 to "СР",
                4 to "ЧТ",
                5 to "ПТ",
                6 to "СБ",
                7 to "ВС"
            )
        } else mapOf(
            1 to "MON",
            2 to "TUE",
            3 to "WED",
            4 to "THU",
            5 to "FRI",
            6 to "SAT",
            7 to "SUN"
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
                    hour = shortDays[indexDaysOfWeek].toString(),
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

        return NTuple5(currentWeatherCurrentData,
            weatherShortList,
            weatherDescription,
            dailyWeatherList,
            getSweepAngle(weatherDailyData.daily.sunrise[0], weatherDailyData.daily.sunset[0]))
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

        companion object {
            fun valueFromRange(num: Int): DescriptionDigest {
                return values().firstOrNull { num in it.wc } ?: ErrorDigest
            }
        }
    }

    enum class DescriptionAQI(val wc: IntRange, val value: Int) {
        Good(0..20, R.string.good),
        Fair(20..40, R.string.fair),
        Moderate(40..60, R.string.moderate),
        Poor(60..80, R.string.poor),
        VeryPoor(80..100, R.string.very_poor),
        HighlyPoor(100..800, R.string.highly_poor),
        Error(IntRange.EMPTY, R.string.error_aqi);

        companion object {
            fun getDescriptionAQI(num: Int): DescriptionAQI {
                return values().firstOrNull { num in it.wc } ?: Error
            }
        }
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

    private fun convertSunRiseOrSet(sunData: String): String {
        val str = sunData.toCharArray()
        var tempString = ""
        (11..15).map {
            tempString += str[it]
        }
        return tempString
    }

    companion object {
        var angle = Float.NaN
    }

    private fun getSweepAngle(rise: String, set: String): Float {

        val sunRiseMinute = LocalDateTime.parse(rise).toEpochSecond(ZoneOffset.UTC)
        val sunSetMinute = LocalDateTime.parse(set).toEpochSecond(ZoneOffset.UTC)
        val nowMinute = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

        angle = if (nowMinute <= sunRiseMinute)
            0f
        else if (nowMinute in sunRiseMinute..sunSetMinute)
            ((nowMinute - sunRiseMinute) / ((sunSetMinute - sunRiseMinute) / 140f))
        else
            140f
        return angle
    }
}

