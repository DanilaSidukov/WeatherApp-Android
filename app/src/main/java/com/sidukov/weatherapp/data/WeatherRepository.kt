package com.sidukov.weatherapp.data

import android.content.Context
import com.sidukov.weatherapp.R

class WeatherRepository(private val context: Context) {
    //WeatherRepository - получает и возвращает данные
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

}