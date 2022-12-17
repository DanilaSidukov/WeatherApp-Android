package com.sidukov.weatherapp.ui.fragment_weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.domain.WeatherShort

// потом потом, добавить DiffCallback или DiffUtilCallback, чтобы данные обновлялись тогда, когда нужно, это позволит избавиться от notifyDataSetChanged(),
// который добавляет определёную сложность
//<> - generic class, он работает с типом объекта, который к нему приходит, в нашем случае с DailyWeatherViewHolder
class DailyWeatherAdapter(
    private var weatherList: List<WeatherShort>
) :
    RecyclerView.Adapter<DailyWeatherAdapter.DailyWeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        //создаётся view объект
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.daily_weather_item, parent, false)
        return DailyWeatherViewHolder(view)
    }

    //onBindViewHolder - привязывает данные к view элементам, объявленным в ViewHolder. Эти данные были отправлены в адаптер (обычно в виде списка)
    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        holder.textDateWeather.text = weatherList[position].hour
        holder.imageDateWeather.setImageResource(weatherList[position].image)
        holder.textDateTemperatureDailyWeather.text = weatherList[position].temperature.toString()
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    //holder - содержит view элементы, представленые в xml layoutе
    class DailyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDateWeather: TextView = itemView.findViewById(R.id.text_date_weather)
        val imageDateWeather: ImageView = itemView.findViewById(R.id.image_date_weather)
        val textDateTemperatureDailyWeather: TextView =
            itemView.findViewById(R.id.text_temperature_date_weather)
    }

    // эта функция принимает лист, который затем назначает внутренней переменной list
    fun updateList(list: List<WeatherShort>) {
        weatherList = list
        notifyDataSetChanged()
    }


}